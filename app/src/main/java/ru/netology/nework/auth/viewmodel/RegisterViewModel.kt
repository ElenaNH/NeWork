package ru.netology.nework.auth.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.authapi.AuthRegApiService
import ru.netology.nework.auth.authdto.RegisterInfo
import ru.netology.nework.auth.authdto.Token
import ru.netology.nework.util.SingleLiveEvent
import ru.netology.nework.auth.authdto.UserResponse
import ru.netology.nework.exept.AlertException
import ru.netology.nework.exept.AlertIncorrectPhotoFormatException
import ru.netology.nework.exept.AlertInfo
import ru.netology.nework.exept.AlertServerAccessingErrorException
import ru.netology.nework.exept.AlertUserAlreadyRegisteredException
import ru.netology.nework.exept.AlertUserNotFoundException
import ru.netology.nework.exept.AlertWrongServerResponseException
import ru.netology.nework.model.PhotoModel
import java.io.File
import javax.inject.Inject

//typealias Rstring = ru.netology.nework.R.string

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RegisterViewModel @Inject constructor(
    application: Application,
    private val appAuth: AppAuth,
    private val authRegApiService: AuthRegApiService,
) : AndroidViewModel(application) {

    // Результат попытки регистрации:
    // Успешный статус будем использовать для автоматического возврата в предыдущий фрагмент
    val isAuthorized: Boolean
        get() = appAuth.data.value != null    // Берем StateFlow и проверяем

    private val _registerSuccessEvent = SingleLiveEvent<Unit>()
    val registerSuccessEvent: LiveData<Unit>
        get() = _registerSuccessEvent

    private val _avatar = MutableLiveData<PhotoModel?>()
    val avatar: LiveData<PhotoModel?>
        get() = _avatar

    // Информация для регистрации и состояние готовности к регистрации:
    // Информация для регистрации должна проверяться на полноту до того, как будет попытка регистрации
    private val _registerInfo =
        MutableLiveData(RegisterInfo())
    val registerInfo: LiveData<RegisterInfo>
        get() = _registerInfo

    private val _alertWarning = MutableLiveData<AlertInfo>(AlertInfo(0, emptyList()))
    val alertWarning: LiveData<AlertInfo>
        get() = _alertWarning

    private var _stateWaiting =
        MutableLiveData<Boolean>(false) // В состоянии ожидания кнопка будет заблокирована
    val stateWaiting: LiveData<Boolean>
        get() = _stateWaiting

    // - - - - - - - - - - - - - - - - -
    //Попытка регистрации

    fun doRegister() {

        if (!completed()) {
            // Если мы все правильно сделали, то эта ошибка не должна возникать
            _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
            return
        }

        // Отправить запрос регистрации на сервер
        viewModelScope.launch {
            _stateWaiting.value = true  // начинаем ждать ответ
            try {
                registerUser()
                if (isAuthorized)
                    _registerSuccessEvent.value = Unit  // Однократное событие
                else {
                    // Этого быть не должно, т.к. registerUser в случае неуспеха выдает ошибку
                    _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
                }
            } catch (alert: AlertException) {
                Log.e("CATCH", "CATCH (ALERT) OF REGISTER USER - $alert")
                _alertWarning.value = alert.alertInfo()

            } catch (e: Exception) {
                Log.e("CATCH", "CATCH OF REGISTER USER - ${e.message.toString()}")
                // Пользователю не доводим причину ошибки. Главное - что мы не умеем ее обрабатывать
                _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
            } finally {
                // Каким бы ни был ответ - он завершает состояние ожидания
                _stateWaiting.value = false  // закончили ждать ответ
            }
        } // end of launch

    }

    private suspend fun registerUser() {

        val noAvatar = (_avatar.value == null) or (_avatar.value?.file == null)

        var responseToken: Response<Token>? = null
        try {
            if (noAvatar) {
                responseToken = authRegApiService.registerUser(
                    registerInfo.value?.login ?: "",
                    registerInfo.value?.password ?: "",
                    registerInfo.value?.username ?: "",
                )
            } else {
                /* // Так создает кавычки вокруг полей:
                    responseToken = authRegApiService.registerWithPhoto(
                    registerInfo.value?.login ?: "",
                    registerInfo.value?.password ?: "",
                    registerInfo.value?.username ?: "",
                    MultipartBody.Part.createFormData(
                        "file",
                        "file",
                        _avatar.value?.file!!.asRequestBody()
                    )
                )*/
                responseToken = authRegApiService.registerWithPhoto(
                    MultipartBody.Part.createFormData("login", registerInfo.value?.login ?: ""),
                    MultipartBody.Part.createFormData("pass", registerInfo.value?.password ?: ""),
                    MultipartBody.Part.createFormData("name", registerInfo.value?.username ?: ""),
                    MultipartBody.Part.createFormData(
                        "file",
                        "file",
                        _avatar.value?.file!!.asRequestBody()
                    )
                )
            }

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw AlertServerAccessingErrorException(e.message.toString())
        }

        if (!(responseToken?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (responseToken.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                403 -> throw AlertUserAlreadyRegisteredException(
                    registerInfo.value?.login ?: "USER"
                )

                415 -> throw AlertIncorrectPhotoFormatException()
                else -> throw AlertWrongServerResponseException(
                    responseToken.code(),
                    responseToken.message()
                )
            }
        }
        val receivedToken = responseToken?.body() ?: throw AlertWrongServerResponseException(
            responseToken.code(),
            "body is null"
        )

        // Надо прогрузить токен в AppAuth
        appAuth.setToken(receivedToken)

        // После логина или регистрации с аватаркой нужно запросить и сохранить имя и аватарку текущего пользователя
        // После регистрации без аватарки все эти данные есть, их нужно только сохранить

        if (noAvatar) {
            val currentLogin = registerInfo.value?.login ?: ""
            val currentName = registerInfo.value?.username ?: ""
            val currentAvatar = ""  // TODO - доделать загрузку аватарки!!!
            appAuth.setCurrentUser(
                UserResponse(
                    receivedToken.id,
                    currentLogin,
                    currentName,
                    currentAvatar
                )
            )
        } else {
            // TODO - запросить аватарку после регистрации (если была аватарка)
            // Запросим всю отображаемую информацию о пользователе
            var responseUserInf: Response<UserResponse>? = null
            try {
                responseUserInf = authRegApiService.getUserById(
                    receivedToken.id,
                )
            } catch (e: Exception) {
                // Обычно сюда попадаем, если нет ответа сервера
                throw AlertServerAccessingErrorException(e.message.toString())
            }

            if (!(responseUserInf.isSuccessful ?: false)) {
                // А сюда попадаем, потому что сервер вернул isSuccessful == false
                when (responseUserInf.code()) {
                    //200 -> true // Сюда не должны попасть из-за верхней проверки
                    404 -> throw AlertUserNotFoundException(receivedToken.id)
                    else -> throw AlertWrongServerResponseException(
                        responseUserInf.code(),
                        responseUserInf.message()
                    )
                }
            }

            val userResponse = responseUserInf.body() ?: throw AlertWrongServerResponseException(
                responseToken.code(),
                "body is null"
            )

            // TODO Обработать ситуацию, когда связь обрубилась после получения токена,
            // но до получения отображаемых данных пользователя
            appAuth.setCurrentUser(userResponse)
        }

    }

    // - - - - - - - - - - - - - - - - -
    // Обработка информации для регистрации

    fun resetRegisterInfo(newRegisterInfo: RegisterInfo) {

        // Новая информация для регистрации
        _registerInfo.value = newRegisterInfo

        // TODO - Подумать, как еще можно обеспечить синхронизацию обработки недостаточных данных
        viewModelScope.launch { delay(300) } // Строго после установки алертов!!!
    }

    fun passwordsMatch() = registerInfo.value?.let {
        (it.password.length > 0)
                && (it.password.toString() == it.password2.toString())
    } ?: false

    fun completed(): Boolean {
        val checkCompletion = registerInfo.value?.let {
            (it.login.length > 0)
                    && (it.password.length > 0)
                    && (it.password2.length > 0)
                    && (passwordsMatch())
                    && (it.username.length > 0)
                    && (!(_stateWaiting?.value ?: false))
        } ?: false

        return checkCompletion
    }

    fun setPhoto(uri: Uri, file: File) {
        _avatar.value = PhotoModel(uri, file)
    }

    fun clearPhoto() {
        _avatar.value = null
    }

}




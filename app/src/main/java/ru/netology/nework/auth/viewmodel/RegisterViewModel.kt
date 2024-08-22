package ru.netology.nework.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nework.auth.api.UserApi // Объект-компаньон
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.dto.RegisterInfo
import ru.netology.nework.auth.dto.Token
import ru.netology.nework.util.SingleLiveEvent
//import ru.netology.nework.R
import ru.netology.nework.auth.dto.UserResponse
import ru.netology.nework.exept.AlertException
import ru.netology.nework.exept.AlertIncorrectPasswordException
import ru.netology.nework.exept.AlertIncorrectPhotoFormatException
import ru.netology.nework.exept.AlertIncorrectUsernameException
import ru.netology.nework.exept.AlertInfo
import ru.netology.nework.exept.AlertServerAccessingErrorException
import ru.netology.nework.exept.AlertUserAlreadyRegisteredException
import ru.netology.nework.exept.AlertWrongServerResponseException

//typealias Rstring = ru.netology.nework.R.string

class RegisterViewModel : ViewModel() {

    // Результат попытки регистрации:
    // Успешный статус будем использовать для автоматического возврата в предыдущий фрагмент
    val isAuthorized: Boolean
        get() = AppAuth.getInstance().data.value != null    // Берем StateFlow и проверяем

    private val _registerSuccessEvent = SingleLiveEvent<Unit>()
    val registerSuccessEvent: LiveData<Unit>
        get() = _registerSuccessEvent

    // Информация для регистрации и состояние готовности к регистрации:
    // Информация для регистрации должна проверяться на полноту до того, как будет попытка регистрации
    private val _registerInfo =
        MutableLiveData(RegisterInfo()) // TODO - отследить нажатие клавиатуры
    val registerInfo: LiveData<RegisterInfo>
        get() = _registerInfo

    private val _completionWarningSet = MutableLiveData(emptySet<Int>())
    val completionWarningSet: LiveData<Set<Int>>
        get() = _completionWarningSet

    private val _alertWarning = MutableLiveData<AlertInfo>(AlertInfo(0, emptyList()))
    val alertWarning: LiveData<AlertInfo>
        get() = _alertWarning

    private var _waitingResponse = false // В состоянии ожидания кнопка будет заблокирована

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
            _waitingResponse = true  // начинаем ждать ответ
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
                _waitingResponse = false  // закончили ждать ответ
            }
        } // end of launch

    }

    private suspend fun registerUser() {
        var response: Response<Token>? = null
        try {
            response = UserApi.retrofitService.registerUser(
                registerInfo.value?.login ?: "",
                registerInfo.value?.password ?: "",
                registerInfo.value?.username ?: "",
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw AlertServerAccessingErrorException(e.message.toString())
        }

        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                403 -> throw AlertUserAlreadyRegisteredException(
                    registerInfo.value?.login ?: "USER"
                )

                415 -> throw AlertIncorrectPhotoFormatException()
                else -> throw AlertWrongServerResponseException(response.code(), response.message())
            }
        }
        val responseToken = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )

        // Надо прогрузить токен в AppAuth
        AppAuth.getInstance().setToken(responseToken)

        // После логина нужно запросить и сохранить имя и аватарку текущего пользователя
        // После регистрации все эти данные есть, их нужно только сохранить

        val currentLogin = registerInfo.value?.login ?: ""
        val currentName = registerInfo.value?.username ?: ""
        val currentAvatar = ""  // TODO - доделать загрузку аватарки!!!
        AppAuth.getInstance().setCurrentUser(
            UserResponse(
                responseToken.id,
                currentLogin,
                currentName,
                currentAvatar
            )
        )

    }

    // - - - - - - - - - - - - - - - - -
    // Обработка информации для регистрации

    fun resetRegisterInfo(newRegisterInfo: RegisterInfo) {

        // Новая информация для регистрации
        _registerInfo.value = newRegisterInfo

        /*// Проверка полноты информации для регистрации, установка набора предупреждений
        val warnIdSet = mutableSetOf<Int>()

        registerInfo.value?.let {
            if (it.login.length == 0) {
                warnIdSet.add(R.string.warning_no_login)
            }
            if (it.password.length == 0) {
                warnIdSet.add(R.string.warning_no_password)
            }
            if (it.password.length != it.password2.length) {
                warnIdSet.add(R.string.warning_no_matching_password)
            }
            if (it.username.length == 0) {
                warnIdSet.add(R.string.warning_no_username)
            }
            //Позже добавим проверку наличия аватара
        }
        _completionWarningSet.value = warnIdSet*/

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
                    && (!(_waitingResponse))
        } ?: false

        return checkCompletion
    }

}

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
import ru.netology.nework.auth.dto.LoginInfo
import ru.netology.nework.auth.dto.Token
import ru.netology.nework.util.SingleLiveEvent
import ru.netology.nework.auth.dto.UserResponse
import ru.netology.nework.exept.AlertException
import ru.netology.nework.exept.AlertIncorrectPasswordException
import ru.netology.nework.exept.AlertIncorrectUsernameException
import ru.netology.nework.exept.AlertInfo
import ru.netology.nework.exept.AlertServerAccessingErrorException
import ru.netology.nework.exept.AlertUserNotFoundException
import ru.netology.nework.exept.AlertWrongServerResponseException

typealias Rstring = ru.netology.nework.R.string

class LoginViewModel : ViewModel() {

    // Результат попытки логина:
    // Успешный статус будем использовать для автоматического возврата в предыдущий фрагмент
    val isAuthorized: Boolean
        get() = AppAuth.getInstance().data.value != null    // Берем StateFlow и проверяем

    private val _loginSuccessEvent = SingleLiveEvent<Unit>()
    val loginSuccessEvent: LiveData<Unit>
        get() = _loginSuccessEvent

    // Информация для входа и состояние готовности ко входу:
    // Информация для входа должна проверяться на полноту до того, как будет попытка входа
    private val _loginInfo = MutableLiveData(LoginInfo())
    val loginInfo: LiveData<LoginInfo>
        get() = _loginInfo

    private val _alertWarning = MutableLiveData<AlertInfo>(AlertInfo(0, emptyList()))
    val alertWarning: LiveData<AlertInfo>
        get() = _alertWarning

    private  var _waitingResponse = false // В состоянии ожидания кнопка будет заблокирована

    // - - - - - - - - - - - - - - - - -
    //Попытка входа

    fun doLogin() {

        if (!completed()) {
            // Если мы все правильно сделали, то эта ошибка не должна возникать
            _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
            return
        }

        // Отправить запрос авторизации на сервер
        viewModelScope.launch {
            _waitingResponse = true  // начинаем ждать ответ
            try {
                updateUser()
                if (isAuthorized)
                    _loginSuccessEvent.value = Unit  // Однократное событие
                else {
                    // Этого быть не должно, т.к. updateUser в случае неуспеха выдает ошибку
                    _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
                }
            } catch (alert: AlertException) {
                Log.e("CATCH", "CATCH (ALERT) OF UPDATE USER - $alert")
                _alertWarning.value = alert.alertInfo()

            } catch (e: Exception) {
                Log.e("CATCH", "CATCH OF UPDATE USER - ${e.message.toString()}")
                // Пользователю не доводим причину ошибки. Главное - что мы не умеем ее обрабатывать
                _alertWarning.value = AlertInfo(Rstring.alert_unknown_error)
            } finally {
                // Каким бы ни был ответ - он завершает состояние ожидания
                _waitingResponse = false  // закончили ждать ответ
            }
        } // end of launch

    }

    private suspend fun updateUser() {
        var response: Response<Token>? = null
        try {
            response = UserApi.retrofitService.updateUser(
                loginInfo.value?.login ?: "",
                loginInfo.value?.password ?: ""
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw AlertServerAccessingErrorException(e.message.toString())
        }

        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                400 -> throw AlertIncorrectPasswordException()
                404 -> throw AlertIncorrectUsernameException(loginInfo.value?.login ?: "USER")
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

        // Запросим всю отображаемую информацию о пользователе
        var responseUserInf: Response<UserResponse>? = null
        try {
            responseUserInf = UserApi.retrofitService.getUserById(
                responseToken.id,
            )
        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw AlertServerAccessingErrorException(e.message.toString())
        }

        if (!(responseUserInf.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (responseUserInf.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                404 -> throw AlertUserNotFoundException(responseToken.id)
                else -> throw AlertWrongServerResponseException(response.code(), response.message())
            }
        }

        val userResponse = responseUserInf.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )


        // TODO Обработать ситуацию, когда связь обрубилась после получения токена,
        // но до получения отображаемых данных пользователя
        AppAuth.getInstance().setCurrentUser(userResponse)

    }


    // - - - - - - - - - - - - - - - - -
    // Обработка информации для входа

    fun resetLoginInfo(newLoginInfo: LoginInfo) {

        // Новая информация для входа
        _loginInfo.value = newLoginInfo

        // TODO - Подумать, как еще можно обеспечить синхронизацию обработки недостаточных данных
        viewModelScope.launch { delay(300) } // Строго после установки алертов!!!
    }

    fun completed(): Boolean {
        val checkCompletion = loginInfo.value?.let {
            (it.login.length > 0) && (it.password.length > 0) && (!(_waitingResponse))
        } ?: false

        return checkCompletion
    }

}

package ru.netology.nework.exept

typealias Rstring = ru.netology.nework.R.string

/** Информация для пользователя, локализуемая в контексте */
data class AlertInfo(
    val rStringCode: Int,
    val args: List<Any> = emptyList()
)

/** Локализуемая в контексте ошибка */
sealed class AlertException(
    val rStringCode: Int,
    cause: Throwable? = null
) :
    Exception("Alert exception", cause) {
    //    open val args: List<Any> = emptyList()
    //    abstract val args: List<Any>
    open fun getArgs(): List<Any> = emptyList()
//    val alertInfo = AlertInfo(rStringCode, getArgs())
    fun alertInfo() = AlertInfo(rStringCode, getArgs()) // каждый раз новый экземпляр??? Неважно!
}

// Неизвестная ошибка (применять при перехвате неизвестной ошибки)
class AlertUnknownException(
    cause: Throwable
) : AlertException(Rstring.alert_unknown_error, cause) {
    override fun toString(): String {
        // Сюда печатаем перехваченную ошибку, а пользователю ее не выводим
        return super.toString().plus(" # Unknown error # $cause")
    }
}

// Проблемы с сервером
class AlertServerUnavailableException(
    cause: Throwable? = null
) : AlertException(Rstring.alert_server_unavailable, cause)

class AlertServerAccessingErrorException(
    val errorText: String = "",
    cause: Throwable? = null
) : AlertException(Rstring.alert_server_accessing_error, cause) {

    override fun getArgs(): List<Any> = listOf(errorText)

    override fun toString(): String {
        return super.toString().plus(" # No server access # $errorText")
    }
}

class AlertWrongServerResponseException(
    val responseCode: Int,
    val responseMessage: String,
    cause: Throwable? = null
) : AlertException(Rstring.alert_wrong_server_response, cause) {

    override fun getArgs(): List<Any> = listOf(responseCode, responseMessage)

    override fun toString(): String {
        return super.toString().plus(" # Wrong server response $responseCode # $responseMessage")
    }
}

// Проблемы с пользователем
class AlertIncorrectPasswordException(
    cause: Throwable? = null
) : AlertException(Rstring.alert_incorrect_password, cause)

class AlertIncorrectUsernameException(
    val username: String,
    cause: Throwable? = null
) : AlertException(Rstring.alert_incorrect_username, cause) {

    override fun getArgs(): List<Any> = listOf(username)

    override fun toString(): String {
        return super.toString().plus(" # username=$username")
    }
}

class AlertUserAlreadyRegisteredException(
    val username: String,
    cause: Throwable? = null
) : AlertException(Rstring.alert_user_already_registered, cause) {

    override fun getArgs(): List<Any> = listOf(username)

    override fun toString(): String {
        return super.toString().plus(" # username=$username")
    }
}

class AlertUserNotFoundException(
    val userId: Long,
    cause: Throwable? = null
) : AlertException(Rstring.alert_user_not_found, cause) {

    override fun getArgs(): List<Any> = listOf(userId)
    override fun toString(): String {
        return super.toString().plus(" # userId=$userId")
    }
}

// Проблемы с медиа
class AlertIncorrectPhotoFormatException(
    cause: Throwable? = null
) : AlertException(Rstring.alert_incorrect_photo_format, cause)





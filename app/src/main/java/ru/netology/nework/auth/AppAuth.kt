package ru.netology.nework.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.auth.authdto.Token
import ru.netology.nework.auth.authdto.UserResponse

class AppAuth private constructor(context: Context) {
    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"
        private const val LAST_ID_KEY = "LAST_ID_KEY"
        private const val LAST_LOGIN_KEY = "LAST_LOGIN_KEY"
        private const val LAST_NAME_KEY = "LAST_NAME_KEY"
        private const val LAST_AVATAR_KEY = "LAST_AVATAR_KEY"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun initApp(context: Context) {
            INSTANCE = AppAuth(context)
        }

        fun getInstance(): AppAuth = requireNotNull(INSTANCE) {
            "You must call initApp before"
        }
    }

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data = MutableStateFlow<Token?>(null) // Всегда можно узнать текущее значение
    val data = _data.asStateFlow()

    // Последный успешно подключенный пользователь (храним на случай недоступности сервера)
    private val prefsLast = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    // Текущий пользователь (эти данные отображаем)
    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser = _currentUser.asStateFlow()


    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L) {
            prefs.edit { clear() }  // Последнего авторизованного юзера не чистим
            _currentUser.value = plugGuest()
        } else {
            _data.value = Token(id, token)
            // Если id совпадает с прошлой авторизацией, то восстановим и значение _currentUser
            val lastId = prefsLast.getLong(LAST_ID_KEY, 0L)
            if (lastId == id) {
                val lastLogin = prefsLast.getString(LAST_LOGIN_KEY, "user") ?: "user"
                val lastName = prefsLast.getString(LAST_NAME_KEY, lastLogin) ?: lastLogin
                val lastAvatar = prefsLast.getString(LAST_AVATAR_KEY, "") ?: ""
                _currentUser.value = UserResponse(lastId, lastLogin, lastName, lastAvatar)
            } else {
                _currentUser.value = plugUser(id)
            }

        }
    }

    @Synchronized
    fun setToken(token: Token) {
        _data.value = token
        prefs.edit(commit = true) {
            putString(TOKEN_KEY, token.token)
            putLong(ID_KEY, token.id)
        }
        // Если id пользователя изменился, то его имя не знаем до прихода ответа сервера
        // Назначим пока имя "user", нужное имя позже установит setCurrentUser
        if (_currentUser.value?.id != token.id)
            _currentUser.value = plugUser(token.id)
    }

    @Synchronized
    fun setCurrentUser(user: UserResponse) {
        val currentId = data.value?.id ?: 0L  // prefs.getLong(ID_KEY, 0L)
        if (user.id == currentId) {  //if (user.id == prefs.getLong(ID_KEY, 0L)) {
            _currentUser.value = user
            prefsLast.edit(commit = true) {
                putLong(LAST_ID_KEY, user.id)
                putString(LAST_LOGIN_KEY, user.login)
                putString(LAST_NAME_KEY, user.name)
                putString(LAST_AVATAR_KEY, user.avatar)
            }
        } else {
            // Нет смысла менять prefsLast, но нужно изменить currentUser (мы не прогружаем id, если он не такой, как в токене)
            _currentUser.value = plugUser(currentId)
        }
    }


    fun clearAuth() {
        _data.value = null
        prefs.edit { clear() }
        // Текущий пользователь стал гостем (но прошлый авторизованный пользователь не изменился)
        _currentUser.value = plugGuest()
    }

    private fun plugGuest() = UserResponse(0L, "guest", "guest", "")

    private fun plugUser(userId: Long) = UserResponse(userId, "user", "user", "")

}

package ru.netology.nework.auth.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.authdto.Token
import ru.netology.nework.auth.authdto.UserResponse

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.asLiveData
//import ru.netology.nework.auth.dto.Token


/*TODO - Пока что все выглядит как пятое колесо к классу AppAuth.
*  Если AppAuth выполняет роль репозитория,
*  тогда тут должен храниться CurrentUser, а из AppAuth пробрасываться сюда LastSuccessfulUser */

class AuthViewModel : ViewModel() {
    /*    val data: LiveData<Token?> = AppAuth.getInstance().data
            .asLiveData()    // Берем StateFlow и преобразуем его к лайвдате*/
    val data = AppAuth.getInstance().data
    val currentUser = AppAuth.getInstance().currentUser
    val currentUserName: String
        get() = if (data.value == null) "Guest" else currentUser.value?.name ?: "User"

    val isAuthorized: Boolean
        get() = AppAuth.getInstance().data.value != null    // Берем StateFlow и проверяем

    // Полностью прокидываем управление на уровень хранения данных авторизации (т.е., в AppAuth)
    fun setToken(token: Token) {
        AppAuth.getInstance().setToken(token)
    }

    /* TODO - скорее всего, именно currentUser должен храниться здесь на уровне модели,
    *   поскольку хранимые данные prefsLast в общем случае не совпадают с CurrentUser */
    fun setCurrentUser(user: UserResponse) {
        AppAuth.getInstance().setCurrentUser(user)
    }

    fun clearAuth() {
        AppAuth.getInstance().clearAuth()
    }

}


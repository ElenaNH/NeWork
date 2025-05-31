package ru.netology.nework.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.authdto.Token
import ru.netology.nework.auth.authdto.UserResponse
import javax.inject.Inject

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.asLiveData
//import ru.netology.nework.auth.dto.Token


/*TODO - Пока что все выглядит как пятое колесо к классу AppAuth.
*  Если AppAuth выполняет роль репозитория,
*  тогда тут должен храниться CurrentUser, а из AppAuth пробрасываться сюда LastSuccessfulUser */

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    val appAuth: AppAuth,
)  : AndroidViewModel(application) {  //: ViewModel() {
    /*    val data: LiveData<Token?> = AppAuth.getInstance().data
            .asLiveData()    // Берем StateFlow и преобразуем его к лайвдате*/
    /*@Inject
    lateinit var appAuth: AppAuth*/

    val data = appAuth.data
    val currentUser = appAuth.currentUser
    val currentUserName: String
        get() = if (data.value == null) "Guest" else currentUser.value?.name ?: "User"

    val isAuthorized: Boolean
        get() = appAuth.data.value != null    // Берем StateFlow и проверяем

    fun storeCurrentUserIdToDb(id: Long) {
        viewModelScope.launch {
            // TODO добавить метод сохранения в appAuth или отсюда прогружать в БД?
            appAuth.setCurrentUserIdToDb() // Текущий id извествен внутри appAuth
        }
    }

    // Полностью прокидываем управление на уровень хранения данных авторизации (т.е., в AppAuth)
    fun setToken(token: Token) {
        appAuth.setToken(token)
    }

//    TODO - скорее всего, именно currentUser должен храниться здесь на уровне модели,
//    поскольку хранимые данные prefsLast в общем случае не совпадают с CurrentUser
    fun setCurrentUser(user: UserResponse) {
        appAuth.setCurrentUser(user)
    }

    fun clearAuth() {
        appAuth.clearAuth()
        viewModelScope.launch {
            appAuth.removeUserIdFromDb()
        }
    }

}


package ru.netology.nework.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.netology.nework.NeWorkApp
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.db.AppDb
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.repository.UserRepositoryImpl

class UserViewModel(application: NeWorkApp) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: UserRepository =
        UserRepositoryImpl(AppDb.getInstance(application).appDao())

    val data = AppAuth.getInstance().data.flatMapLatest { token ->
        repository.data
            //.map{}   // Тут можно преобразовать данные, рассчитать вычисляемые поля
    } //.asLiveData(Dispatchers.Default) // Тут можно преобразовать к лайвдате, если захотим


    // Создание модели
    init{
        reloadUsers()
    }

    private fun reloadUsers() = viewModelScope.launch {
        // TODO - добавить работу со статусами

        try {
            repository.getAllUsers()
        } catch (e: Exception) {
            Log.e("ERR", "Catch of repository.getAllUsers() error")
        }
    }

}

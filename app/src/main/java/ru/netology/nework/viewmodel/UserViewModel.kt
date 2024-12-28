package ru.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.User
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.repository.UserRepositoryImpl

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository =
        UserRepositoryImpl(AppDb.getInstance(application).appDao())

    // Все пользователи
    val data: Flow<List<User>> = AppAuth.getInstance().data.flatMapLatest { token ->
        repository.data
        //.map{}   // Тут можно преобразовать данные, рассчитать вычисляемые поля
    } //.asLiveData(Dispatchers.Default) // Тут можно преобразовать к лайвдате, если захотим

    // Выбранный пользователь

    val selected = MutableLiveData(User.getEmptyUser())
    val selectedJobs: MutableLiveData<List<Job>> = MutableLiveData(emptyList())

    // Создание модели
    init {
        reloadUsers()
    }

    //private
    fun reloadUsers() = viewModelScope.launch {
        // TODO - добавить работу со статусами

        try {
            repository.getAllUsers()
        } catch (e: Exception) {
            Log.e("ERR", "Catch of repository.getAllUsers() error")
        }
    }

    fun selectUser(user: User) {
        selected.value = user
        if (user.id != 0L) reloadSelectedJobs()
    }

    fun reloadSelectedJobs() {
        viewModelScope.launch {

            try {
                selected.value?.let {
                    selectedJobs.value = repository.getUserJobsById(it.id)
                }
            } catch (e: Exception) {
                Log.e("ERR", "Catch of repository.getUserJobsById(${selected.value?.id}) error")
            }
        }
    }

}



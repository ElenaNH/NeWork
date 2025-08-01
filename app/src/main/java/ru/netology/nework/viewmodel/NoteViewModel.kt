package ru.netology.nework.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import ru.netology.nework.api.DataApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.AppDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.User
import ru.netology.nework.repository.UserRepository
import ru.netology.nework.repository.UserRepositoryImpl
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    application: Application,
    private val appAuth: AppAuth,
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : AndroidViewModel(application) {

    private val repository: UserRepository =
        UserRepositoryImpl(appDao, dataApiService)
    // UserRepositoryImpl(AppDb.getInstance(application).appDao())

    // Все пользователи
    val data: Flow<List<User>> = appAuth.data.flatMapLatest { token ->
        repository.data
        //.map{}   // Тут можно преобразовать данные, рассчитать вычисляемые поля
    } //.asLiveData(Dispatchers.Default) // Тут можно преобразовать к лайвдате, если захотим


}
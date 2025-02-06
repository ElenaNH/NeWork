package ru.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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

    private val _selected = MutableLiveData(User.getEmptyUser())
    val selected: LiveData<User>      // = selected as LiveData<User>
        get() = _selected
    private val _selectedJobs: MutableLiveData<List<Job>> = MutableLiveData(emptyList())
    val selectedJobs: LiveData<List<Job>>
        get() = _selectedJobs
    private val _editedJob: MutableLiveData<Job> =
        MutableLiveData(Job.emptyJobOfUser(_selected.value?.id ?: 0L))
    val editedJob: LiveData<Job>
        get() = _editedJob

    // Выбранная вкладка
    private val _menuTabIndex: MutableLiveData<Int> = MutableLiveData(0)
    val menuTabIndex: LiveData<Int>
        get() = _menuTabIndex


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
        _selected.value = user
        if (user.id != 0L) reloadSelectedJobs()
        updateTabIndex(0)
    }

    fun reloadSelectedJobs() {
        viewModelScope.launch {

            try {
                _selected.value?.let {
                    _selectedJobs.value = repository.getUserJobsById(it.id)
                }
            } catch (e: Exception) {
                Log.e("ERR", "Catch of repository.getUserJobsById(${_selected.value?.id}) error")
            }
        }
    }

    fun removeJob(job: Job) {
        viewModelScope.launch {
            try {
                repository.removeJob(job.id) // Удаляем работу
                _selectedJobs.value =
                    repository.getUserJobsById(job.userId)  // Обновляем список работ

            } catch (e: Exception) {
                Log.e("ERR", "Catch of repository.removeJob (${job.id}) error")
            }
        }
    }

    fun saveJob(job: Job) {
        viewModelScope.launch {
            try {
                Log.d("Job saving", "start...")
                _editedJob.value = repository.saveJob(job) // Сохраняем работу в БД и в модели
                _selectedJobs.value =
                    repository.getUserJobsById(job.userId)  // Обновляем список работ
                Log.d("Job saving", "end... ${_editedJob.value}")

            } catch (e: Exception) {
                Log.e("ERR UserViewModel", "Catch of repository.saveJob (${job.id}) error")
            }
        }
    }

    fun setEditedJob(job: Job) {
        // TODO проверить, что всегда будет правильный userId (либо переустанавливать его)
        _editedJob.value = job
    }

    fun clearEditedJob() {
        _editedJob.value = Job.emptyJobOfUser(_selected.value?.id ?: 0L)
    }

    fun updateTabIndex(newTabIndex: Int) {
        _menuTabIndex.value = newTabIndex
    }

}



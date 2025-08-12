package ru.netology.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nework.api.DataApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Note
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.NoteType
import ru.netology.nework.repository.NoteRepository
import ru.netology.nework.repository.PostRepositoryImpl

abstract class NoteViewModel(
    application: Application,
    private val appAuth: AppAuth,
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : AndroidViewModel(application) {

    // Вычисляемый тип сообщения
    private val noteType by lazy { if (this is PostViewModel) NoteType.POST else NoteType.EVENT }

    //abstract
    private val repository: NoteRepository = when {
        (this is PostViewModel) -> PostRepositoryImpl(appDao, dataApiService)
        else -> PostRepositoryImpl(appDao, dataApiService)
    }

    open val data: Flow<List<Note>> =
        repository.data  // Flow<List<Note>> - либо Flow<List<Post>>, либо Flow<List<Event>>

    // Выбранная заметка (пост или событие)    //MutableLiveData<Note>
    private val _selected: MutableLiveData<Note> = when (noteType) {
        (NoteType.POST) -> MutableLiveData(Post.getEmptyPost())
        (NoteType.EVENT) -> MutableLiveData(Event.getEmptyEvent())
    }

    val selected: LiveData<Note> = _selected
//    val selected = when (noteType) {
//        NoteType.POST -> _selected as LiveData<Post>
//        NoteType.EVENT -> _selected as LiveData<Event>
//    }

    fun reloadNotes() = viewModelScope.launch {
        // TODO - добавить работу со статусами

        try {
            repository.getAll()
        } catch (e: Exception) {
            Log.e("ERR", "Catch of repository.getAll() error")
        }
    }

    fun selectNote(note: Note) {
        _selected.value = note
//        = when (noteType) {
//            NoteType.POST -> note as Post
//            NoteType.EVENT -> note as Event
//        }
    }


}
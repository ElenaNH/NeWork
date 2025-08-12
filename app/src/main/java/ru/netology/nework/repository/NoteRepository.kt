package ru.netology.nework.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.DataApiService
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Note
import ru.netology.nework.entity.NoteEntity
import ru.netology.nework.entity.NoteQueryMe
import ru.netology.nework.entity.fromDto
import ru.netology.nework.entity.toDto
import ru.netology.nework.enumeration.NoteType
import ru.netology.nework.except.AlertWrongServerResponseException

abstract class NoteRepository(
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
    val noteType: NoteType,
    val currentUserWall: Boolean = false,
    noteAuthorId: Long? = null,  // Если currentUserWall = true, то здесь должно быть NULL (проигнорируем эти данные, есть не null)
) {
    val authorId = if (currentUserWall) null else noteAuthorId

    open val data: Flow<List<Note>> =
        appDao.getAllNotesFlow(noteType.noteTypeCode, currentUserWall, authorId)
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    open suspend fun getAll(): List<Note> {

        // Запросим список постов/событий с сервера
        val response = when (noteType) {
            NoteType.POST ->
                if (currentUserWall)
                    dataApiService.getMyWall()
                else dataApiService.getAllPosts()

            NoteType.EVENT -> dataApiService.getAllEvents()
        }

        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                else -> throw AlertWrongServerResponseException(
                    response.code(),
                    response.message()
                )
            }
        }
        val noteResponseList = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )

        // Непустой список постов или событий сохраняем в БД
        if (noteResponseList.count() > 0) {
            val testingSave = noteResponseList.fromDto()
            appDao.insertNote(testingSave)
            // TODO - Возможно, следует использовать appDao.saveNoteWithLists - единый коммит на две таблицы
        }

        val testingGet = appDao.getAllNotesChoice(noteType.noteTypeCode, currentUserWall, authorId)
        val notes = testingGet.let(List<NoteQueryMe>::toDto)

        return notes
    }

//    abstract suspend fun getAllPosts(): List<Post>
//    abstract suspend fun getAllEvents(): List<Event>
//    abstract suspend fun getPostById(id: Long): Post?
//    abstract suspend fun getEventById(id: Long): Event?

    open suspend fun getById(id: Long): Note? {
        val noteQuery = appDao.getNoteById(id, noteType.noteTypeCode)
        return noteQuery?.toDto()
    }

    suspend fun remove(note: Note) {
        appDao.deleteNote(NoteEntity.fromDto(note))
    }

    suspend fun removeById(id: Long) {
        appDao.deleteNoteById(id, noteType.noteTypeCode)
    }

    suspend fun saveNote(note: Note) {
        appDao.insertNote(NoteEntity.fromDto(note))
    }
}


/*interface NoteRepository {
    val data: Flow<List<Note>>

    suspend fun getAllPosts(): List<Post>
    suspend fun getAllEvents(): List<Event>

    suspend fun getPostById(id: Long): Post?
    suspend fun getEventById(id: Long): Event?

    suspend fun removeNote(note: Note)
    suspend fun removePost(id: Long)
    suspend fun removeEntity(id: Long)

    suspend fun saveNote(note: Note): Note
}*/
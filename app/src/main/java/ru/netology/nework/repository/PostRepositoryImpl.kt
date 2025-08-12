package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import ru.netology.nework.api.DataApiService
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Note
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.NoteType
import javax.inject.Inject
import kotlin.collections.map

class PostRepositoryImpl @Inject constructor(
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : NoteRepository(appDao, dataApiService, NoteType.POST)
{
    override val data: Flow<List<Post>>
        get() = super.data.map{notes -> notes as List<Post>}  //get() = super.data.mapLatest{notes -> notes as List<Post>}

    override suspend fun getAll(): List<Post> {
        val notes = super.getAll()
        return notes.map{note -> note as Post}  //notes as List<Post>
    }

    override suspend fun getById(id: Long): Post? {
        val note = super.getById(id)
        return note as? Post
    }

}
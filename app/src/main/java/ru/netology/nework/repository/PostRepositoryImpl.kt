package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.api.DataApiService
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Note
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.NoteType
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : NoteRepository(appDao, dataApiService, NoteType.POST)

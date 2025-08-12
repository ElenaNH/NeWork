package ru.netology.nework.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.api.DataApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepositoryImpl
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    application: Application,
    private val appAuth: AppAuth,
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : NoteViewModel(application, appAuth, appDao, dataApiService)   //AndroidViewModel(application)
{
//override
    private val repository: PostRepositoryImpl =
        PostRepositoryImpl(appDao, dataApiService)

    override val data: Flow<List<Post>> = repository.data




}
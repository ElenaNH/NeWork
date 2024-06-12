package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.dto.Token

class AuthViewModel : ViewModel() {
//    val data: LiveData<Token?> = AppAuth.getInstance().data
//        .asLiveData()    // Берем StateFlow и преобразуем его к лайвдате

    val dataFlow: StateFlow<Token?> = AppAuth.getInstance().data

    val data: LiveData<Token?> = dataFlow.asLiveData()

    val isAuthorized: Boolean
        get() = AppAuth.getInstance().data.value != null    // Берем StateFlow и проверяем
}


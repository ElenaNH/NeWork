package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.auth.authdto.UserResponse

interface UserRepository {
    val data: Flow<UserResponse>
    suspend fun getAll()
}

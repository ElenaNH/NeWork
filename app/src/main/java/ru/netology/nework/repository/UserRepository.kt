package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.User

interface UserRepository {
    val data: Flow<List<User>>
    suspend fun fillInitial()
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User?
}

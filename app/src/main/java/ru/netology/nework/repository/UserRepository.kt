package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.User

interface UserRepository {
    val data: Flow<List<User>>
    suspend fun fillInitial()
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User?
    //jobs
    suspend fun getUserJobsById(userId: Long): List<Job>
    suspend fun removeJob(id: Long)
    suspend fun saveJob(job: Job): Job
}

package ru.netology.nework.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.User
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.toLocalDto

class UserRepositoryImpl(private val appDao: AppDao) : UserRepository {
    override val data: Flow<List<User>> =
        /*get() =*/ appDao.getAllUsers()
        .map { it.toLocalDto() }
        .flowOn(Dispatchers.Default)

    /*override suspend fun getAllUsers() {
        TODO("Not yet implemented")
    }*/

    /*override suspend fun getUserById(id: Long): User? {
        return appDao.getUserById(id)
            .firstOrNull()
            .let { it?.toLocalDto() }
    }*/
}

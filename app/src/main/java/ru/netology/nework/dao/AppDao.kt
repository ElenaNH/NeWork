package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.UserEntity

@Dao
interface AppDao {
    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT count(*) FROM UserEntity")
    fun countUsers(): Int

    @Query("SELECT COUNT(*)  == 0 FROM UserEntity")
    //suspend
    fun isEmpty(): Boolean

    /* // Компилится тольк БЕЗ suspend, хотя в учебном проекте аналогичное было с suspend
    @Query("SELECT * FROM UserEntity")
    suspend fun getAllUsersAlternate(): List<UserEntity>*/

    /*@Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUserById(id:Long): List<UserEntity>*/

    /*@Query("SELECT UserEntity.* FROM UserEntity, AuthEntity WHERE (UserEntity.id == AuthEntity.id) AND AuthEntity.authenicated")
    suspend fun getCurrentUser(): List<UserEntity>*/

}

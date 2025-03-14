package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Job
import ru.netology.nework.entity.AuthEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.UserJobEntity
import ru.netology.nework.entity.UserListTypeEntity

@Dao
interface AppDao {

    // Первичное заполнение данных

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initialUserFilling(userListType: UserListTypeEntity)

    // Current user (authenicated)

    @Query("SELECT COUNT(*) == 0 FROM AuthEntity WHERE authenicated")
    suspend fun emptyCurrentUser(): Boolean

    @Query("SELECT id FROM AuthEntity WHERE authenicated")
    suspend fun getCurrentUserId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentUserId(authEntity: AuthEntity)

    @Query("DELETE FROM AuthEntity")
    suspend fun clearCurrentUserId()

    // Users

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity")
    suspend fun getAllUsersAlternate(): List<UserEntity>

    @Query("SELECT COUNT(*) == 0 FROM UserEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUserById(id: Long): List<UserEntity>

    @Query("SELECT UserEntity.* FROM UserEntity, AuthEntity WHERE (UserEntity.id == AuthEntity.id) AND AuthEntity.authenicated")
    suspend fun getCurrentUser(): List<UserEntity>

    /*@Query("SELECT count(*) FROM UserEntity")
    fun countUsers(): Long*/

    // User jobs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserJobs(userJobs: List<UserJobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserJob(userJob: UserJobEntity)

    @Query("SELECT * FROM UserJobEntity WHERE userId = :userId")
    suspend fun getJobsByUserId(userId: Long): List<UserJobEntity>

    @Query("SELECT * FROM UserJobEntity WHERE id = :id")
    suspend fun getJobById(id: Long): List<UserJobEntity>

    @Query("DELETE FROM UserJobEntity WHERE id = :id")
    suspend fun removeJobById(id: Long)

    @Query("DELETE FROM UserJobEntity WHERE userId = :userId")
    suspend fun clearJobsByUserId(userId: Long)

}

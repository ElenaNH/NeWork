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
import ru.netology.nework.entity.UserQueryMe

// interface AppDao ЗАМЕНЕН на abstract class AppDao; во все методы добавлено объявление 'abstract'
// это позволит в дайльнейшем использовать аннотацию @Transaction
@Dao
abstract class AppDao {

    // Первичное заполнение данных

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun initialUserFilling(userListType: UserListTypeEntity)

    // Current user (authenicated)

    @Query("SELECT COUNT(*) == 0 FROM AuthEntity WHERE authenicated")
    suspend abstract fun emptyCurrentUser(): Boolean

    @Query("SELECT id FROM AuthEntity WHERE authenicated")
    suspend abstract fun getCurrentUserId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun setCurrentUserId(authEntity: AuthEntity)

    @Query("DELETE FROM AuthEntity")
    suspend abstract fun clearCurrentUserId()

    // Users

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUser(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUser(user: UserEntity)

    //@Query("SELECT * FROM UserEntity")
    @Query("SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
            "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
            "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id")
    abstract fun getAllUsers(): Flow<List<UserQueryMe>>
    //abstract fun getAllUsers(): Flow<List<UserEntity>>

    //@Query("SELECT * FROM UserEntity")
    @Query("SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
            "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
            "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id")
    suspend abstract fun getAllUsersAlternate(): List<UserQueryMe>
    //suspend abstract fun getAllUsersAlternate(): List<UserEntity>

    @Query("SELECT COUNT(*) == 0 FROM UserEntity")
    suspend abstract fun isEmpty(): Boolean

//    @Query("SELECT * FROM UserEntity WHERE id = :id")
@Query("SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
        "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
        "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id " +
        "WHERE UserEntity.id = :id")
suspend abstract fun getUserById(id: Long): List<UserQueryMe>
//    suspend abstract fun getUserById(id: Long): List<UserEntity>

    @Query("SELECT UserEntity.* FROM UserEntity, AuthEntity " +
            "WHERE (UserEntity.id == AuthEntity.id) AND AuthEntity.authenicated")
    suspend abstract fun getCurrentUser(): List<UserEntity>

    /*@Query("SELECT count(*) FROM UserEntity")
    fun countUsers(): Long*/

    // User jobs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUserJobs(userJobs: List<UserJobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUserJob(userJob: UserJobEntity)

    @Query("SELECT * FROM UserJobEntity WHERE userId = :userId")
    suspend abstract fun getJobsByUserId(userId: Long): List<UserJobEntity>

    @Query("SELECT * FROM UserJobEntity WHERE id = :id")
    suspend abstract fun getJobById(id: Long): List<UserJobEntity>

    @Query("DELETE FROM UserJobEntity WHERE id = :id")
    suspend abstract fun removeJobById(id: Long)

    @Query("DELETE FROM UserJobEntity WHERE userId = :userId")
    suspend abstract fun clearJobsByUserId(userId: Long)

    // TODO Можно использовать транзакцию для заполнения данными двух связанных таблиц
    // TODO Для этого мы сделали АБСТРАКТНЫЙ КЛАСС, вместо интерфейса DAO
    // TODO https://startandroid.ru/ru/courses/architecture-components/27-course/architecture-components/531-urok-7-room-insert-update-delete-transaction.html



}

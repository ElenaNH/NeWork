package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.AuthEntity
import ru.netology.nework.entity.NoteEntity
import ru.netology.nework.entity.NoteQueryMe
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

    @Query(
        "SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
                "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
                "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id"
    )
    abstract fun getAllUsersFlow(): Flow<List<UserQueryMe>>

    @Query(
        "SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
                "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
                "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id"
    )
    suspend abstract fun getAllUsersChoice(): List<UserQueryMe>

    @Query("SELECT COUNT(*) == 0 FROM UserEntity")
    suspend abstract fun isEmpty(): Boolean

    @Query(
        "SELECT UserEntity.id, UserEntity.name, UserEntity.avatar, " +
                "(AuthEntity.id == UserEntity.id AND AuthEntity.authenicated) AS ownedByMe " +
                "FROM UserEntity LEFT JOIN AuthEntity ON UserEntity.id == AuthEntity.id " +
                "WHERE UserEntity.id == :id"
    )
    suspend abstract fun getUserById(id: Long): List<UserQueryMe>

    @Query(
        "SELECT UserEntity.* FROM UserEntity, AuthEntity " +
                "WHERE (UserEntity.id == AuthEntity.id) AND AuthEntity.authenicated"
    )
    suspend abstract fun getCurrentUser(): List<UserEntity>


    // User jobs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUserJobs(userJobs: List<UserJobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend abstract fun insertUserJob(userJob: UserJobEntity)

    @Query("SELECT * FROM UserJobEntity WHERE userId == :userId")
    suspend abstract fun getJobsByUserId(userId: Long): List<UserJobEntity>

    @Query("SELECT * FROM UserJobEntity WHERE id == :id")
    suspend abstract fun getJobById(id: Long): List<UserJobEntity>

    @Query("DELETE FROM UserJobEntity WHERE id == :id")
    suspend abstract fun removeJobById(id: Long)

    @Query("DELETE FROM UserJobEntity WHERE userId == :userId")
    suspend abstract fun clearJobsByUserId(userId: Long)

    // TODO Можно использовать транзакцию для заполнения данными двух связанных таблиц
    // TODO Для этого мы сделали АБСТРАКТНЫЙ КЛАСС, вместо интерфейса DAO
    // TODO https://startandroid.ru/ru/courses/architecture-components/27-course/architecture-components/531-urok-7-room-insert-update-delete-transaction.html

    //@Query("SELECT * FROM NoteEntity WHERE noteTypeCode == :noteTypeCode")
    @Query(
        "SELECT NoteEntity.*, A.authenicated AS ownedByMe FROM NoteEntity " +
                "LEFT JOIN (SELECT id, authenicated FROM AuthEntity WHERE authenicated) AS A  " +
                "ON NoteEntity.authorId == A.id " +
                "WHERE noteTypeCode == :noteTypeCode " +
                "And (Not :currentUserOnly Or A.authenicated) " +
                "And (CASE WHEN :authorId Is Null THEN 1==1 ELSE NoteEntity.authorId == :authorId END)"
    )
    abstract fun getAllNotesFlow(
        noteTypeCode: Int,
        currentUserOnly: Boolean = false,
        authorId: Long? = null
    ): Flow<List<NoteQueryMe>>

//    fun getAllPosts() = getAllNotes(NoteType.POST.noteTypeCode).mapLatest { it.map(NoteEntity::toDto) }
//    fun getAllEvents() = getAllNotes(NoteType.EVENT.noteTypeCode).mapLatest { it.map(NoteEntity::toDto) }

    @Query(
        "SELECT NoteEntity.*, A.authenicated AS ownedByMe FROM NoteEntity " +
                "LEFT JOIN (SELECT id, authenicated FROM AuthEntity WHERE authenicated) AS A  " +
                "ON NoteEntity.authorId = A.id " +
                "WHERE noteTypeCode == :noteTypeCode " +
                "And (Not :currentUserOnly Or A.authenicated) " +
                "And (CASE WHEN :authorId Is Null THEN 1==1 ELSE NoteEntity.authorId == :authorId END)"
    )
    abstract fun getAllNotesChoice(
        noteTypeCode: Int,
        currentUserOnly: Boolean = false,
        authorId: Long? = null
    ): List<NoteQueryMe>

    //@Query("SELECT * FROM NoteEntity WHERE id == :id and noteTypeCode == :noteTypeCode")
    @Query(
        "SELECT NoteEntity.*, A.authenicated AS ownedByMe FROM NoteEntity " +
                "LEFT JOIN (SELECT id, authenicated FROM AuthEntity WHERE authenicated) AS A  " +
                "ON NoteEntity.authorId == A.id " +
                "WHERE NoteEntity.id == :id and noteTypeCode == :noteTypeCode"
    )
    abstract suspend fun getNoteById(
        id: Long,
        noteTypeCode: Int,
    ): NoteQueryMe?

    @Query("DELETE FROM NoteEntity WHERE id == :id and noteTypeCode == :noteTypeCode")
    abstract suspend fun deleteNoteById(id: Long, noteTypeCode: Int)

    @Delete
    abstract suspend fun deleteNote(noteEntity: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNote(notes: List<NoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNote(note: NoteEntity)

}

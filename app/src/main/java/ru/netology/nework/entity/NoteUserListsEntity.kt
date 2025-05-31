package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import ru.netology.nework.enumeration.UserListType


//https://habr.com/ru/articles/713518/   // TODO - Foreign key


@Entity(
    primaryKeys = ["noteTypeCode", "noteEntityId", "userListTypeCode", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["noteTypeCode", "id"],
            childColumns = ["noteTypeCode", "noteEntityId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ]
)
data class NoteUserListsEntity(
    val noteTypeCode: Int, // enum class NoteType(val noteTypeCode: Int)
    val noteEntityId: Long,
    val userListTypeCode: Int, // enum class UserListType(val code: Int, val marker: String)
    val userId: Long,
)

package ru.netology.nework.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.netology.nework.auth.authdto.UserResponse
import ru.netology.nework.dto.User

data class UserQueryMe(
    //@ColumnInfo(name = "id")
    val id: Long,
    //@ColumnInfo(name = "name")
    val name: String,
    //@ColumnInfo(name = "avatar")
    val avatar: String,
    //@ColumnInfo(name = "ownedByMe")
    val ownedByMe: Boolean = false,
) {
    fun toLocalDto() = User(id, name, avatar, ownedByMe)

    companion object {
        fun fromRemoteDto(dto: UserResponse) = UserQueryMe(
            dto.id, dto.name, dto.avatar ?: "",
            false,   // В базу должно попасть только false, а реальное значение будет расчетным
        )

        fun fromLocalDto(dto: User) = UserQueryMe(
            dto.id, dto.name, dto.avatar,
            false,   // В базу должно попасть только false, а реальное значение будет расчетным
        )
    }

}

// Функции расширения для списков

fun List<UserQueryMe>.toLocalDto(): List<User> = map(UserQueryMe::toLocalDto)


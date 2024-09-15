package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.auth.authdto.UserResponse
import ru.netology.nework.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val avatar: String,
) {
    fun toLocalDto() = User(id, name, avatar)

    companion object {
        fun fromRemoteDto(dto: UserResponse) = UserEntity(dto.id, dto.name, dto.avatar)
        fun fromLocalDto(dto: User) = UserEntity(dto.id, dto.name, dto.avatar)
    }

}

// Функции расширения для списков

fun List<UserEntity>.toLocalDto(): List<User> = map(UserEntity::toLocalDto)

fun List<UserResponse>.fromRemoteDto(): List<UserEntity> = map(UserEntity::fromRemoteDto)


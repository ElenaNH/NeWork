 package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.netology.nework.auth.authdto.UserResponse
import ru.netology.nework.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val avatar: String,
) {

    companion object {
        fun fromRemoteDto(dto: UserResponse) = UserEntity(
            dto.id, dto.name, dto.avatar ?: "",
        )

        // TODO Понадобится, если будем сохранять в локальную БД перед отправкой на сервер (пока не хотим)
        fun fromLocalDto(dto: User) = UserEntity(
            dto.id, dto.name, dto.avatar,
        )
    }

}

// Функции расширения для списков

fun List<UserResponse>.fromRemoteDto(): List<UserEntity> = map(UserEntity::fromRemoteDto)


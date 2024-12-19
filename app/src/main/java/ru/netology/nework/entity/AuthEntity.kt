package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AuthEntity (
    val id: Long,                                 // id пользователя
    @PrimaryKey val authenicated: Boolean = true, // Действующая запись authenicated=true
)

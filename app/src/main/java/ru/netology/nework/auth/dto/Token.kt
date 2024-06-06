package ru.netology.nework.auth.dto

data class Token(
    val id: Long,   // id пользователя
    val token: String,
)

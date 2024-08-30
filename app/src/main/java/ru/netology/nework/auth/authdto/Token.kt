package ru.netology.nework.auth.authdto

data class Token(
    val id: Long,   // id пользователя
    val token: String,
)

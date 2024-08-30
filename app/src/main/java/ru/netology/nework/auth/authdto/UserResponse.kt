package ru.netology.nework.auth.authdto

data class UserResponse(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String,
)

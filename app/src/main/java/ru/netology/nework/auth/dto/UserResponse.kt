package ru.netology.nework.auth.dto

data class UserResponse(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String,
)

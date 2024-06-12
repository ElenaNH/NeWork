package ru.netology.nework.auth.dto

data class RegisterInfo(
    val username: String = "",
    val login: String = "",
    val password: String = "",
    val password2: String = "",
    val avatar: String? = null,
)

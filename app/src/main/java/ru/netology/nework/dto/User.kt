package ru.netology.nework.dto

data class User(
    val id: Long,
    val name: String,
    val avatar: String,
) {
    companion object {
        fun getEmptyUser() = User(0, "", "")
    }
}

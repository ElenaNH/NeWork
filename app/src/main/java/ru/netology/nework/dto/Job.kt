package ru.netology.nework.dto

data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
    val userId: Long,
) {
    constructor(userId: Long) :
            this(0L, "", "", "", "", null, userId)

    companion object {
        fun emptyJobOfUser(userId: Long) = Job(userId)
    }
}

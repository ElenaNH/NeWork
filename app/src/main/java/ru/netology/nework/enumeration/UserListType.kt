package ru.netology.nework.enumeration

enum class UserListType(val code: Int, val marker: String) {
    // Нельзя использовать ordinal, потому что он может поменяться, но мы хотим сохранить эти данные в БД неизменными
    likeOwnerIds(0, "likedByMe"),
    mentionIds(1, "mentionedMe"),
    speakerIds(2, ""),
    participantIds(3, "participatedByMe"),
}

package ru.netology.nework.dto

import ru.netology.nework.enumeration.EventType
import ru.netology.nework.enumeration.NoteType

// TODO - позже разобьем на 3 отдельных файла

abstract class Note(
    open val id: Long = 0L,
    open val authorId: Long,
    open val author: String,
    open val authorJob: String? = null,
    open val authorAvatar: String? = null,
    open val content: String = "",
    open val published: String,
    open val coords: Coordinates? = null,
    open val link: String? = null,
    open val likeOwnerIds: List<Long> = emptyList(),
    open val likedByMe: Boolean = false,
    open val attachment: Attachment? = null,
    open val users: Map<String, UserPreview> = emptyMap(),
    open val datetime: String, // Для постов - пустая строка
    open val type: EventType?,  // Для постов null, для событий not null
) {
    // Вычисляемый тип сообщения
     val noteType: NoteType
        get() = if (type == null) NoteType.POST else NoteType.EVENT
}

data class Post(
    override val id: Long,
    override val authorId: Long,
    override val author: String,
    override val authorJob: String?,
    override val authorAvatar: String?,
    override val content: String,
    override val published: String,
    override val coords: Coordinates?,
    override val link: String? = null,
    override val likeOwnerIds: List<Long> = emptyList(),
    override val likedByMe: Boolean = false,
    override val attachment: Attachment?,
    override val users: Map<String, UserPreview> = emptyMap(),
    // НЕ ПЕРЕОПРЕДЕЛЯТЬ datetime,
    // НЕ ПЕРЕОПРЕДЕЛЯТЬ type,
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
) : Note(
    id,
    authorId,
    author,
    authorJob,
    authorAvatar,
    content,
    published,
    coords,
    link,
    likeOwnerIds,
    likedByMe,
    attachment,
    users,
    "", // datetime пусто
    null,  // type не относится к посту, поэтому строго пусто
)

data class Event(
    override val id: Long,
    override val authorId: Long,
    override val author: String,
    override val authorJob: String?,
    override val authorAvatar: String?,
    override val content: String,
    override val published: String,
    override val coords: Coordinates?,
    override val link: String? = null,
    override val likeOwnerIds: List<Long> = emptyList(),
    override val likedByMe: Boolean = false,
    override val attachment: Attachment?,
    override val users: Map<String, UserPreview> = emptyMap(),
    override val datetime: String,
    override val type: EventType,   // Строго не пусто!!
    val speakerIds: List<Long> = emptyList(),
    val participantIds: List<Long> = emptyList(),
    val participatedByMe: Boolean = false,
) : Note(
    id,
    authorId,
    author,
    authorJob,
    authorAvatar,
    content,
    published,
    coords,
    link,
    likeOwnerIds,
    likedByMe,
    attachment,
    users,
    datetime,
    type
)

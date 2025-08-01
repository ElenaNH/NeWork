package ru.netology.nework.entity

import androidx.room.Embedded
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Note
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.enumeration.NoteType

class NoteQueryMe(
    val noteTypeCode: Int,  // TODO - Foreign key, чтобы ограничиться константами из перечисления NoteType
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String = "",
    val published: String,
    @Embedded(prefix = "coord")
    val coords: InnerCoordinates? = null,
    val link: String? = null,
    @Embedded(prefix = "attach")
    val attachment: InnerAttachment? = null,
    val datetime: String, // Для постов - пустая строка
    val type: String?,  // Для постов null, для событий not null
    val ownedByMe: Boolean = false,
) {
    // TODO - в реальности объект dto формируется из нескольких таблиц (не участвует JobEntity)
    // TODO - нужен класс NoteSuite, со свойствами Note=Post/Event, [Map<id:String,User>]-список хэшей, CurrentUser:UserResponse
    // TODO - МОЖНО ЛИ сделать entity, который не добавлен в БД в качестве таблицы, но может возвращаться запросом?
    // TODO - как быть с хэшем users?, с массивами?
    fun toDto(): Note =
        if (noteTypeCode == NoteType.POST.noteTypeCode)
            Post(
                id = id,
                authorId = authorId,
                author = author,
                authorJob = authorJob,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                coords = coords?.toDto(),
                link = link,
                attachment = attachment?.toDto(),
                ownedByMe = ownedByMe,
            )
        else
            Event(
                id = id,
                authorId = authorId,
                author = author,
                authorJob = authorJob,
                authorAvatar = authorAvatar,
                content = content,
                published = published,
                coords = coords?.toDto(),
                link = link,
                attachment = attachment?.toDto(),
                datetime = datetime,
                type = EventType.valueOf(type!!),
                ownedByMe = ownedByMe,
            )

    companion object {

        fun fromDto(note: Note) =
            NoteQueryMe(
                note.noteType.noteTypeCode,
                id = note.id,
                authorId = note.authorId,
                author = note.author,
                authorJob = note.authorJob,
                authorAvatar = note.authorAvatar,
                content = note.content,
                published = note.published,
                coords = InnerCoordinates.fromDto(note.coords),
                link = note.link,
                attachment = InnerAttachment.fromDto(note.attachment),
                datetime = if (note is Event) note.datetime else "",
                type = if (note is Event) note.type.name else null,
                ownedByMe = note.ownedByMe,
            )
    }

}


// Функции расширения для списков

fun List<NoteQueryMe>.toDto(): List<Note> = map(NoteQueryMe::toDto)

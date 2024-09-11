package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Note
import ru.netology.nework.dto.Post
import ru.netology.nework.enumeration.EventType
import ru.netology.nework.enumeration.NoteType

@Entity(primaryKeys = ["noteTypeCode", "id"])
data class NoteEntity(
    val noteTypeCode: Int,  // TODO - как ограничиться константами из перечисления NoteType
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String = "",
    val published: String,
    @Embedded
    val coords: InnerCoordinates? = null,
    val link: String? = null,
    @Embedded
    val attachment: InnerAttachment? = null,
    val datetime: String, // Для постов - пустая строка
    val type: String?,  // Для постов null, для событий not null
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
            )
}

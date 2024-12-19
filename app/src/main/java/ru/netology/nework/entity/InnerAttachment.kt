package ru.netology.nework.entity

import ru.netology.nework.dto.Attachment
import ru.netology.nework.enumeration.AttachmentType

data class InnerAttachment(
    var url: String,
    var type: String,
) {
    fun toDto() = Attachment(url, AttachmentType.valueOf(type))

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            InnerAttachment(it.url, it.type.toString())
        }
    }
}

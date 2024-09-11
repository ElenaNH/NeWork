package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Job

@Entity
data class UserJobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?
) {
    fun toDto() = Job(
        id,
        name,
        position,
        start,
        finish,
        link,
    )

    companion object {
        fun fromDto(job: Job) =
            UserJobEntity(
                job.id,
                job.name,
                job.position,
                job.start,
                job.finish,
                job.link,
            )
    }
}

fun List<UserJobEntity>.toDto(): List<Job> = map(UserJobEntity::toDto)
fun List<Job>.toEntity(): List<UserJobEntity> = map(UserJobEntity::fromDto)

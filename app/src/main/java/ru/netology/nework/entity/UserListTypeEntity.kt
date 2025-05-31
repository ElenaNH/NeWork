package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["userListType"], unique = true)])
data class UserListTypeEntity(
    @PrimaryKey val userListTypeCode: Int,
    val userListType: String, // уникальные значения в поле определены в аннотации
    val userListTypeMarker: String = "",
)

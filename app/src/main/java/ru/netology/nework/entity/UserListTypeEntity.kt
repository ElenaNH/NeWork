package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserListTypeEntity(
    @PrimaryKey val userListTypeCode: Int,
    val userListType: String, //@ColumnInfo(index = true) TODO - сделать уникальные значения в поле
    val userListTypeMarker: String = "",
)

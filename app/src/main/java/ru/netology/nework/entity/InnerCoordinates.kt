package ru.netology.nework.entity

import ru.netology.nework.dto.Coordinates

data class InnerCoordinates(
    val lat: Double = 0.0,
    val long: Double = 0.0,
) {
    fun toDto() = Coordinates(lat, long)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            InnerCoordinates(it.lat, it.long)
        }
    }
}

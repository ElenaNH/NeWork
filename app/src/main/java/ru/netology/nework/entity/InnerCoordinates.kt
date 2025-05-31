package ru.netology.nework.entity

import ru.netology.nework.dto.Coordinates

data class InnerCoordinates(
    var lat: Double = 0.0,
    var long: Double = 0.0,
) {
    fun toDto() = Coordinates(lat, long)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            InnerCoordinates(it.lat, it.long)
        }
    }
}

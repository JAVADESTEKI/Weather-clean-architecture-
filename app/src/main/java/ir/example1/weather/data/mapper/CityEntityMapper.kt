package ir.example1.weather.data.mapper

import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.domain.model.City
import kotlin.String
import kotlin.time.Clock.System.now


fun City.toEntity(): CityEntity {
    val now = System.currentTimeMillis()
    return CityEntity(
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        selectedAt = now
    )
}

fun CityEntity.toDomain(): City {
    return City(
        id= id,
        name= name,
        country= country,
        lat= lat,
        lon= lon,
        selectetAt= selectedAt
    )
}
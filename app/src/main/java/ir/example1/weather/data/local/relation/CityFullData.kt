package ir.example1.weather.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity

data class CityFullData(

    @Embedded
    val city: CityEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val weather: WeatherEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val forecasts: List<ForecastEntity>?
)

package ir.example1.weather.data.mapper

import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.domain.model.Forecast

fun Forecast.toEntity(): ForecastEntity {
    return ForecastEntity(
        cityId,
        dateTime,
        dateText,
        temperature,
        feelsLike,
        minTemp,
        maxTemp,
        humidity,
        pressure,
        windSpeed,
        description,
        icon,
        condition,
        clouds,
        rain,
        pop
    )
}

fun ForecastEntity.toDomain(): Forecast {
    return Forecast(
        cityId,
        dateTime,
        dateText,
        temperature,
        feelsLike,
        minTemp,
        maxTemp,
        humidity,
        pressure,
        windSpeed,
        description,
        icon,
        condition,
        clouds,
        rain,
        pop
    )
}

package ir.example1.weather.data.mapper

import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.domain.model.Weather

fun Weather.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityId = 0,
        cityName = cityName,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        humidity = humidity,
        rain = rain,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDegree,
        description = description,
        icon = icon,
        condition = condition,
        clouds = clouds,
        visibility = visibility,
        sunrise = sunrise,
        sunset = sunset,
        country = country,
        lat = lat,
        lon = lon,
        timestamp = timestamp
    )
}

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        cityName = cityName,
        temperature = temperature,
        feelsLike = feelsLike,
        minTemp = minTemp,
        maxTemp = maxTemp,
        humidity = humidity,
        rain = rain,
        pressure = pressure,
        windSpeed = windSpeed,
        windDegree = windDegree,
        description = description,
        icon = icon,
        condition = condition,
        clouds = clouds,
        visibility = visibility,
        sunrise = sunrise,
        sunset = sunset,
        country = country,
        lat = lat,
        lon = lon,
        timestamp = timestamp
    )
}

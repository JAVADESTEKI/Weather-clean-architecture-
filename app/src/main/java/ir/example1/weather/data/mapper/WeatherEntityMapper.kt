package ir.example1.weather.data.mapper

import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.domain.model.Weather

fun Weather.toEntity(): WeatherEntity {
    return WeatherEntity(
        id = id,
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
        lat = coord.lat,
        lon = coord.lon,
        timestamp = timestamp
    )
}

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        id = id,
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
        coord = Weather.Coord(lat, lon),
        timestamp = timestamp
    )
}

package ir.example1.weather.util

import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.data.local.relation.CityFullData
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather

object TestDataFactory {

    fun cityEntity(
        id: Long = 1L,
        name: String = "Tehran",
        lat: Double = 35.7,
        lon: Double = 51.4,
        country: String = "IR",
        selectedAt: Long = 23135454
    ) = CityEntity(
        id = id,
        name = name,
        lat = lat,
        lon = lon,
        country = country,
        selectedAt = selectedAt
    )

    fun weatherEntity(temp: Double = 25.0) = WeatherEntity(
        cityId = 1L,
        temperature = temp,
        description = "Clear",
        icon = "01d",
        cityName = "tehran",
        feelsLike = 30.0,
        minTemp = 25.1,
        maxTemp = 32.0,
        humidity = 26,
        rain = 10.0,
        pressure = 56,
        windSpeed = 21.0,
        windDegree = 6,
        condition = "test",
        clouds = 21,
        visibility = 10000,
        sunrise = null,
        sunset = null,
        country = "IR",
        lat = 1.0,
        lon = 2.0,
        timestamp = 13353
    )

    fun forecastEntity(temp: Double = 20.0) = ForecastEntity(
        temperature = temp,
        description = "Cloudy",
        icon = "02d",
        cityId = 1L,
        dateTime = 3213132,
        dateText = "1231312",
        feelsLike = 35.0,
        minTemp = 25.0,
        maxTemp = 40.0,
        humidity = 25,
        pressure = 25,
        windSpeed = 2.1,
        condition = "test",
        clouds = 20,
        rain = 10.0,
        pop = 35.0
    )


    fun cityDomain(
        name: String = "Tehran",
        lat: Double = 35.7,
        lon: Double = 51.4,
        country: String = "IR",
        selectedAt: Long = 23135454,
        localName: String = "tehrn"
    ) = City(
        name = name,
        lat = lat,
        lon = lon,
        id = 1,
        country = country,
        selectedAt = selectedAt,
        localName = localName
    )

    fun weatherDomain(temp: Double = 25.0) = Weather(
        temperature = temp,
        description = "Clear",
        icon = "01d",
        cityName = "tehran",
        feelsLike = 30.0,
        minTemp = 25.1,
        maxTemp = 32.0,
        humidity = 26,
        rain = 10.0,
        pressure = 56,
        windSpeed = 21.0,
        windDegree = 6,
        condition = "test",
        clouds = 21,
        visibility = 10000,
        sunrise = null,
        sunset = null,
        country = "IR",
        lat = 1.0,
        lon = 2.0,
        timestamp = 13353
    )

    fun forecastDomain(temp: Double = 20.0) = Forecast(
        temperature = temp,
        description = "Cloudy",
        icon = "02d",
        cityId = 1L,
        dateTime = 3213132,
        dateText = "1231312",
        feelsLike = 35.0,
        minTemp = 25.0,
        maxTemp = 40.0,
        humidity = 25,
        pressure = 25,
        windSpeed = 2.1,
        condition = "test",
        clouds = 20,
        rain = 10.0,
        pop = 35.0
    )

    fun cityFullDAtaEntity() = CityFullData(
        city = cityEntity(),
        weather = weatherEntity(),
        forecasts = listOf(forecastEntity())
    )
}
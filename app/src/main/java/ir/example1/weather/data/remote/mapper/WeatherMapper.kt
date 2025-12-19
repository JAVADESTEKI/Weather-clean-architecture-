// data/remote/mapper/WeatherMapper.kt
package ir.example1.weather.data.remote.mapper

import ir.example1.weather.data.remote.response.CurrentWeatherResponse
import ir.example1.weather.domain.model.Weather
import javax.inject.Inject

class WeatherMapper @Inject constructor() {
    fun mapToWeather(response: CurrentWeatherResponse): Weather {
        return Weather(
            id = response.id ?: 0,
            cityName = response.name ?: "",
            temperature = response.main?.temp ?: 0.0,
            feelsLike = response.main?.feelsLike ?: 0.0,
            minTemp = response.main?.tempMin ?: 0.0,
            maxTemp = response.main?.tempMax ?: 0.0,
            humidity = response.main?.humidity ?: 0,
            rain= response.rain?.h ?: 0.0,
            pressure = response.main?.pressure ?: 0,
            windSpeed = response.wind?.speed ?: 0.0,
            windDegree = response.wind?.deg ?: 0,
            description = response.weather?.firstOrNull()?.description ?: "",
            icon = response.weather?.firstOrNull()?.icon ?: "",
            condition = response.weather?.firstOrNull()?.main ?: "",
            clouds = response.clouds?.all ?: 0,
            visibility = response.visibility,
            sunrise = response.sys?.sunrise?.toLong(),
            sunset = response.sys?.sunset?.toLong(),
            country = response.sys?.country ?: "",
            coord = Weather.Coord(
                lat = response.coord?.lat ?: 0.0,
                lon = response.coord?.lon ?: 0.0
            ),
            timestamp = (response.dt ?: 0).toLong() * 1000
        )
    }
}
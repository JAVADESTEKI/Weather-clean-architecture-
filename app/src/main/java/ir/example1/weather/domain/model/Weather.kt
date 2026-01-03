// domain/model/Weather.kt
package ir.example1.weather.domain.model

data class Weather(
    var cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val rain: Double,
    val pressure: Int,
    val windSpeed: Double,
    val windDegree: Int,
    val description: String,
    val icon: String,
    val condition: String,
    val clouds: Int,
    val visibility: Int?,
    val sunrise: Long?,
    val sunset: Long?,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long
)
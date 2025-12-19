// domain/model/Forecast.kt
package ir.example1.weather.domain.model

data class Forecast(
    val id: Int,
    val dateTime: Long,
    val dateText: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String,
    val condition: String,
    val clouds: Int,
    val rain: Double?,
    val pop: Double?
)
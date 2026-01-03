package ir.example1.weather.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather",
    foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["cityId", "timestamp"]
)


data class WeatherEntity(
    val cityId: Long,
    val cityName: String,
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

package ir.example1.weather.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast",
            foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["cityId", "dateText"]
)
data class ForecastEntity(
    val cityId: Long,
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


package ir.example1.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "cities",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val selectedAt: Long // زمان آخرین انتخاب برای تشخیص آخرین شهر
)

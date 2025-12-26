package ir.example1.weather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.example1.weather.data.local.dao.ForecastDao
import ir.example1.weather.data.local.dao.WeatherDao
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.data.local.entity.CityEntity

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class, CityEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
    abstract fun cityDao(): CityDao
}

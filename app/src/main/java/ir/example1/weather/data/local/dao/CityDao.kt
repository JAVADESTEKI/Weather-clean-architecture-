package ir.example1.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.example1.weather.data.local.dto.CityFullData
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<ForecastEntity>)


    @Transaction
    @Query("SELECT * FROM cities ORDER BY selectedAt DESC LIMIT 1")
    suspend fun getLastSelected(): CityFullData?

    @Transaction
    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityFullData(cityId: Long): CityFullData
}

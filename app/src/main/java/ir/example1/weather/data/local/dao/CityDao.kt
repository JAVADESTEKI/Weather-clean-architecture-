package ir.example1.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.example1.weather.data.local.relation.CityFullData
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.domain.model.City

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<ForecastEntity>)


    @Transaction
    @Query("SELECT * FROM cities WHERE id= :cityId ORDER BY selectedAt DESC LIMIT 1")
    suspend fun getLastSelectedCityFullData(cityId: Long): CityFullData?

    @Transaction
    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityFullData(cityId: Long): CityFullData

    @Query("SELECT * FROM cities ORDER BY selectedAt DESC")
    suspend fun getAllCities(): List<CityEntity>

    @Query("DELETE FROM cities WHERE id = :cityId")
    suspend fun deleteCityById(cityId: Long?)

    @Query("SELECT id FROM cities ORDER BY selectedAt DESC LIMIT 1")
    suspend fun getLastInsertedId(): Long



    @Query("DELETE FROM weather WHERE cityId = :cityId")
    suspend fun deleteWeatherByCityId(cityId: Long)

    @Query("DELETE FROM forecast WHERE cityId = :cityId")
    suspend fun deleteForecastsByCityId(cityId: Long)


    @Query("SELECT * FROM cities WHERE id= :cityId ORDER BY selectedAt DESC LIMIT 1")
    suspend fun getLastSelectedCity(cityId: Long): CityEntity?


    @Transaction
    suspend fun updateWeatherForecasts(cityId: Long,weather: WeatherEntity, forecasts: List<ForecastEntity>) {
        deleteWeatherByCityId(cityId)
        deleteForecastsByCityId(cityId)
        insertWeather(weather)
        insertForecasts(forecasts)
    }

}

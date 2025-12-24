package ir.example1.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.example1.weather.data.local.entity.ForecastEntity

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecast")
    suspend fun getForecast(): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(list: List<ForecastEntity>)

    @Query("DELETE FROM forecast")
    suspend fun clear()
}

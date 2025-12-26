package ir.example1.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.example1.weather.data.local.entity.CityEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(city: CityEntity)

    @Query("SELECT * FROM cities ORDER BY selectedAt DESC LIMIT 1")
    suspend fun getLastSelected(): CityEntity?

    @Query("SELECT * FROM cities ORDER BY selectedAt DESC")
    suspend fun getAll(): List<CityEntity>

    @Query("DELETE FROM cities WHERE lat = :lat AND lon = :lon")
    suspend fun deleteByLatLon(lat: Double, lon: Double)
}

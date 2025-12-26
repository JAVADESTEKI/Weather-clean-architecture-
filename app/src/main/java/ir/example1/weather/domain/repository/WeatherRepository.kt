package ir.example1.weather.domain.repository

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        name: String,
        unit: String,
        forceRefresh: Boolean = false
    ): Result<Weather>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        forceRefresh: Boolean = false
    ): Result<List<Forecast>>

    suspend fun searchCities(query: String, limit: Int): Result<List<City>>

    // مدیریت شهرهای ذخیره‌شده
    suspend fun saveSelectedCity(city: City)
    suspend fun getLastSelectedCity(): City?

    // کش
    fun getCachedWeather(): Flow<Weather?>
    suspend fun cacheWeather(weather: Weather)
}

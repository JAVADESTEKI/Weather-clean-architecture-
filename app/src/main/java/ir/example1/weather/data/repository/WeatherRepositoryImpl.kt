// data/repository/WeatherRepositoryImpl.kt
package ir.example1.weather.data.repository

import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiServices,
    private val weatherMapper: WeatherMapper,
    private val forecastMapper: ForecastMapper,
    private val cityMapper: CityMapper,
    private val apiKey: String
) : WeatherRepository {

    private val cachedWeather = MutableStateFlow<Weather?>(null)

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String
    ): Result<Weather> {
        return try {
            val response = apiService.getCurrentWeather(lat, lon, unit, apiKey)
            val weather = weatherMapper.mapToWeather(response)
            cachedWeather.value = weather
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String
    ): Result<List<Forecast>> {
        return try {
            val response = apiService.getForecastWeather(lat, lon, unit, apiKey)
            val forecastList = forecastMapper.mapToForecastList(response)
            Result.success(forecastList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchCities(
        query: String,
        limit: Int
    ): Result<List<City>> {
        return try {
            val response = apiService.getCitiesList(query, limit, apiKey)
            val cities = cityMapper.mapToCityList(response)
            Result.success(cities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedWeather(): Flow<Weather?> {
        return cachedWeather.asStateFlow()
    }

    override suspend fun cacheWeather(weather: Weather) {
        cachedWeather.value = weather
    }
}
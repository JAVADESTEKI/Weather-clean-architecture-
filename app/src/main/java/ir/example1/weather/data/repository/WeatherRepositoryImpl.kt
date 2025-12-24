// data/repository/WeatherRepositoryImpl.kt
package ir.example1.weather.data.repository

import ir.example1.weather.data.local.dao.ForecastDao
import ir.example1.weather.data.local.dao.WeatherDao
import ir.example1.weather.data.mapper.toDomain
import ir.example1.weather.data.mapper.toEntity
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
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiServices,
    private val weatherMapper: WeatherMapper,
    private val forecastMapper: ForecastMapper,
    private val cityMapper: CityMapper,
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao,
    private val apiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        name: String,
        unit: String
    ): Result<Weather> {
        return try {
            val response = apiService.getCurrentWeather(lat, lon, unit, apiKey)
            val weather = weatherMapper.mapToWeather(response)
            weather.cityName= name

            weatherDao.clear()
            weatherDao.insertWeather(weather.toEntity())

            Result.success(weather)
        } catch (e: Exception) {
            val cached = weatherDao.getWeather()
            cached?.let {
                Result.success(it.toDomain())
            } ?: Result.failure(e)
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

            forecastDao.clear()
            forecastDao.insertForecast(forecastList.map { it.toEntity() })

            Result.success(forecastList)
        } catch (e: Exception) {
            val cached = forecastDao.getForecast()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun searchCities(
        query: String,
        limit: Int
    ): Result<List<City>> {
        return try {
            val response = apiService.getCitiesList(query, limit, apiKey)
            Result.success(cityMapper.mapToCityList(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedWeather() = flowOf(null)

    override suspend fun cacheWeather(weather: Weather) {}
}
package ir.example1.weather.data.repository

import ir.example1.weather.data.local.dao.ForecastDao
import ir.example1.weather.data.local.dao.WeatherDao
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.entity.CityEntity
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
import kotlinx.coroutines.flow.flow
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
    private val cityDao: CityDao,
    private val apiKey: String
) : WeatherRepository {

    private val STALE_MS = 2 * 60 * 60 * 1000L // 2 ساعت

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        name: String,
        unit: String,
        forceRefresh: Boolean
    ): Result<Weather> {
        return try {
            val cached = weatherDao.getWeather()
            val now = System.currentTimeMillis()

            val canUseCache =
                !forceRefresh &&
                        cached != null &&
                        cached.lat == lat &&
                        cached.lon == lon &&
                        (now - cached.timestamp) < STALE_MS

            if (canUseCache) {
                Result.success(cached!!.toDomain())
            } else {
                val response = apiService.getCurrentWeather(lat, lon, unit, apiKey)
                val weather = weatherMapper.mapToWeather(response).apply { cityName = name }

                // کش را با شهر جدید جایگزین می‌کنیم
                weatherDao.clear()
                weatherDao.insertWeather(weather.toEntity())

                Result.success(weather)
            }
        } catch (e: Exception) {
            val cached = weatherDao.getWeather()
            cached?.let { Result.success(it.toDomain()) } ?: Result.failure(e)
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        forceRefresh: Boolean
    ): Result<List<Forecast>> {
        return try {
            val cachedWeather = weatherDao.getWeather()
            val now = System.currentTimeMillis()
            val isStale = cachedWeather == null || (now - cachedWeather.timestamp) >= STALE_MS
            val shouldFetch = forceRefresh || isStale ||
                    cachedWeather.lat != lat || cachedWeather.lon != lon

            if (!shouldFetch) {
                val cached = forecastDao.getForecast()
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toDomain() })
                } else {
                    // اگر پیش‌بینی کش نیست ولی آب‌وهوا کش تازه است، از API می‌گیریم
                    val response = apiService.getForecastWeather(lat, lon, unit, apiKey)
                    val forecastList = forecastMapper.mapToForecastList(response)
                    forecastDao.clear()
                    forecastDao.insertForecast(forecastList.map { it.toEntity() })
                    Result.success(forecastList)
                }
            } else {
                val response = apiService.getForecastWeather(lat, lon, unit, apiKey)
                val forecastList = forecastMapper.mapToForecastList(response)
                forecastDao.clear()
                forecastDao.insertForecast(forecastList.map { it.toEntity() })
                Result.success(forecastList)
            }
        } catch (e: Exception) {
            val cached = forecastDao.getForecast()
            if (cached.isNotEmpty()) Result.success(cached.map { it.toDomain() })
            else Result.failure(e)
        }
    }

    override suspend fun searchCities(query: String, limit: Int): Result<List<City>> {
        return try {
            val response = apiService.getCitiesList(query, limit, apiKey)
            Result.success(cityMapper.mapToCityList(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // مدیریت شهر ذخیره‌شده
    override suspend fun saveSelectedCity(city: City) {
        val now = System.currentTimeMillis()
        val entity = CityEntity(
            name = city.name,
            country = city.country,
            lat = city.lat,
            lon = city.lon,
            selectedAt = now
        )
        cityDao.insertOrUpdate(entity)

        // وقتی شهر جدید انتخاب می‌شود، کش آب‌وهوا و پیش‌بینی را پاک می‌کنیم تا برای شهر جدید دوباره گرفته شود
        weatherDao.clear()
        forecastDao.clear()
    }

    override suspend fun getLastSelectedCity(): City? {
        val entity = cityDao.getLastSelected()
        return entity?.let {
            City(name = it.name, country = it.country, lat = it.lat, lon = it.lon)
        }
    }

    override fun getCachedWeather(): Flow<Weather?> = flow {
        emit(weatherDao.getWeather()?.toDomain())
    }

    override suspend fun cacheWeather(weather: Weather) {
        weatherDao.insertWeather(weather.toEntity())
    }
}
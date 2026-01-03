package ir.example1.weather.data.repository


import androidx.room.Transaction
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.dto.CityFullData
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.data.mapper.toDomain
import ir.example1.weather.data.mapper.toEntity
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherMapper: WeatherMapper,
    private val forecastMapper: ForecastMapper,
    private val apiService: ApiServices,
    private val cityMapper: CityMapper,
    private val cityDao: CityDao,
    private val apiKey: String
) : WeatherRepository {

    private val STALE_MS = 2 * 60 * 60 * 1000L // 2 ساعت


    override suspend fun getCurrentWea(
        lat: Double,
        lon: Double,
        name: String,
        unit: String
    ):Result<Weather>{
        return try{
            val response = apiService.getCurrentWeather(lat, lon, unit, apiKey)
            val weather = weatherMapper.mapToWeather(response).apply { cityName = name }
            Result.success(weather)
        }
        catch (e:Exception){
            Result.failure(e)
        }

    }


    override suspend fun getForecastWea(
        lat: Double,
        lon: Double,
        unit: String
    ):Result<List<Forecast>>{
        return try{
            val response = apiService.getForecastWeather(lat, lon, unit, apiKey)
                val forecastList = forecastMapper.mapToForecastList(response)
                Result.success(forecastList)
        }
        catch (e:Exception){
            Result.failure(e)
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


    override suspend fun getLastSelectedCity(): CityFullData? {
        val entity = cityDao.getLastSelected()

        return entity

    }

//    override fun getCachedWeather(): Flow<Weather?> = flow {
//        emit(weatherDao.getWeather()?.toDomain())
//    }
//
//    override suspend fun cacheWeather(weather: Weather) {
//        weatherDao.insertWeather(weather.toEntity())
//    }

    @Transaction
    override suspend fun saveCityFullData(
        city: City,
        weather: Weather,
        forecasts: List<Forecast>
    ) {
        val cityId = cityDao.insertCity(city.toEntity())

        cityDao.insertWeather(
            weather.toEntity().copy(cityId = cityId)
        )

        cityDao.insertForecasts(
            forecasts.map { it.toEntity().copy(cityId = cityId) }
        )
    }

    override suspend fun getCityFullData(cityId: Long): CityWeatherForecast {
        return cityDao
            .getCityFullData(cityId)
            .toDomain()
    }

}
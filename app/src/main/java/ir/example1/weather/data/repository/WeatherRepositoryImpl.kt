package ir.example1.weather.data.repository


import androidx.room.Transaction
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.mapper.toDomain
import ir.example1.weather.data.mapper.toEntity
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.di.IoDispatcher
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherMapper: WeatherMapper,
    private val forecastMapper: ForecastMapper,
    private val apiService: ApiServices,
    private val cityMapper: CityMapper,
    private val cityDao: CityDao,
    private val apiKey: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    private companion object {
        const val STALE_MS = 2 * 60 * 60 * 1000L // 2 ساعت
    }

    // -------------------- Remote --------------------
    override suspend fun getCurrentWea(
        lat: Double, lon: Double, name: String, unit: String
    ): Result<Weather> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getCurrentWeather(lat, lon, unit, apiKey)
            weatherMapper.mapToWeather(response).apply { cityName = name }
        }
    }


    override suspend fun getForecastWea(
        lat: Double, lon: Double, unit: String
    ): Result<List<Forecast>> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getForecastWeather(lat, lon, unit, apiKey)
            forecastMapper.mapToForecastList(response)
        }
    }


    override suspend fun searchCities(query: String, limit: Int): Result<List<City>> =
        withContext(ioDispatcher) {
            runCatching {
                val response = apiService.getCitiesList(query, limit, apiKey)
                cityMapper.mapToCityList(response)
            }
        }

    override suspend fun searchCitiesReverse(
        lat: Double, lon: Double, limit: Int
    ): Result<List<City>> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getCitiesListByLatLon(lat, lon, limit, apiKey)
            cityMapper.mapToCityList(response)
        }
    }

    // -------------------- Local --------------------
    override suspend fun getLastSelectedCityFullData(cityId: Long): CityWeatherForecast? =
        withContext(ioDispatcher) {
            cityDao.getLastSelectedCityFullData(cityId)?.toDomain()
        }

    @Transaction
    override suspend fun saveCityFullData(
        city: City, weather: Weather, forecasts: List<Forecast>
    ): Long = withContext(ioDispatcher) {

        val cityId = cityDao.insertCity(city.toEntity())

        cityDao.insertWeather(
            weather.toEntity().copy(cityId = cityId)
        )

        cityDao.insertForecasts(
            forecasts.map { it.toEntity().copy(cityId = cityId) })

        cityId
    }

    override suspend fun getSavedCities(): List<City> = withContext(ioDispatcher) {
        cityDao.getAllCities().map { it.toDomain() }
    }


    override suspend fun deleteCity(cityId: Long?) = withContext(ioDispatcher) {
        cityDao.deleteCityById(cityId)
    }


    override suspend fun getLastInsertedIdUseCase(): Long = withContext(ioDispatcher) {
        cityDao.getLastInsertedId()
    }


    @Transaction
    override suspend fun updateCityFullData(
        cityId: Long, weather: Weather, forecasts: List<Forecast>
    ) = withContext(ioDispatcher) {

        val weatherEntity = weather.toEntity().copy(cityId = cityId)
        val forecastEntities = forecasts.map { it.toEntity().copy(cityId = cityId) }

        cityDao.updateWeatherForecasts(cityId, weatherEntity, forecastEntities)
    }

    override suspend fun getLastSelectedCity(cityId: Long): City? = withContext(ioDispatcher) {
        cityDao.getLastSelectedCity(cityId)?.toDomain()
    }
}
package ir.example1.weather.data.repository


import androidx.room.Transaction
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.relation.CityFullData
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


    override suspend fun getLastSelectedCityFullData(cityId:Long): CityWeatherForecast? {
        val entity = cityDao.getLastSelectedCityFullData(cityId) //CityFullData (dao relation)

        return entity?.toDomain() // CityWeatherForecast (domain model)

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
    ) :Long{
        val cityId = cityDao.insertCity(city.toEntity())

        cityDao.insertWeather(
            weather.toEntity().copy(cityId = cityId)
        )

        cityDao.insertForecasts(
            forecasts.map { it.toEntity().copy(cityId = cityId) }
        )
        return cityId
    }

    override suspend fun getSavedCities(): List<City> {
        return cityDao.getAllCities().map { it.toDomain() }
    }

    override suspend fun deleteCity(cityId: Long?) {
        cityDao.deleteCityById(cityId)
    }


    override suspend fun getLastInsertedIdUseCase():Long{
        return cityDao.getLastInsertedId()
    }


    @Transaction
    override suspend fun updateCityFullData(cityId:Long,weather: Weather, forecasts: List<Forecast>){

        val weather= weather.toEntity().copy(cityId = cityId)
        val forecasts=forecasts.map { it.toEntity().copy(cityId = cityId) }
        cityDao.updateWeatherForecasts(cityId,weather, forecasts)

    }

    override suspend fun getLastSelectedCity(cityId:Long): City?{
        return cityDao.getLastSelectedCity(cityId)?.toDomain()
    }
}
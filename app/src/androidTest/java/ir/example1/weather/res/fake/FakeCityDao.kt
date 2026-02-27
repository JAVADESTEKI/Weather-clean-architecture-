package ir.example1.weather.data.fake

import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.data.local.relation.CityFullData

class FakeCityDao : CityDao {

    override suspend fun getLastSelectedCityFullData(cityId: Long) = null
    override suspend fun getCityFullData(cityId: Long): CityFullData {
        TODO("Not yet implemented")
    }

    override suspend fun insertCity(city: CityEntity): Long = 1L
    override suspend fun insertWeather(weather: WeatherEntity) {}
    override suspend fun insertForecasts(forecasts: List<ForecastEntity>) {}
    override suspend fun getAllCities(): List<CityEntity> = emptyList()
    override suspend fun deleteCityById(cityId: Long?) {}
    override suspend fun getLastInsertedId(): Long = 1L
    override suspend fun deleteWeatherByCityId(cityId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteForecastsByCityId(cityId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeatherForecasts(
        cityId: Long,
        weather: WeatherEntity,
        forecasts: List<ForecastEntity>
    ) {}
    override suspend fun getLastSelectedCity(cityId: Long) = null
}
package ir.example1.weather.domain.repository

import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.relation.CityFullData
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather

interface WeatherRepository {

    suspend fun getCurrentWea(
        lat: Double,
        lon: Double,
        name: String,
        unit: String
    ):Result<Weather>

    suspend fun getForecastWea(
        lat: Double,
        lon: Double,
        unit: String
    ):Result<List<Forecast>>
    suspend fun searchCities(query: String, limit: Int): Result<List<City>>

    // مدیریت شهرهای ذخیره‌شده

    suspend fun getLastSelectedCityFullData(cityId:Long): CityWeatherForecast?

    suspend fun saveCityFullData(
        city: City,
        weather: Weather,
        forecasts: List<Forecast>
    ):Long

    suspend fun getSavedCities(): List<City>
    suspend fun deleteCity(cityId: Long?)

    suspend fun getLastInsertedIdUseCase():Long

    suspend fun updateCityFullData(cityId:Long,weather: Weather, forecasts: List<Forecast>)

    suspend fun getLastSelectedCity(cityId:Long): City?
}

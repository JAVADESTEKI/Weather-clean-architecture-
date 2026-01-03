package ir.example1.weather.domain.repository

import ir.example1.weather.data.local.dto.CityFullData
import ir.example1.weather.data.local.entity.CityEntity
import ir.example1.weather.data.local.entity.ForecastEntity
import ir.example1.weather.data.local.entity.WeatherEntity
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

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

    suspend fun getLastSelectedCity(): CityFullData?




    suspend fun saveCityFullData(
        city: City,
        weather: Weather,
        forecasts: List<Forecast>
    )

    suspend fun getCityFullData(cityId: Long): CityWeatherForecast
}

// data/remote/api/ApiServices.kt
package ir.example1.weather.data.remote.api

import ir.example1.weather.data.remote.response.CityResponse
import ir.example1.weather.data.remote.response.CurrentWeatherResponse
import ir.example1.weather.data.remote.response.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface ApiServices {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String,
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String,
    ): ForecastResponse

    @GET("geo/1.0/direct")
    suspend fun getCitiesList(
        @Query("q") q: String,
        @Query("limit") limit: Int = 10,
        @Query("appid") apiKey: String
    ): CityResponse

    @GET("geo/1.0/reverse")
    suspend fun getCitiesListByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 10,
        @Query("appid") apiKey: String
    ): CityResponse
}
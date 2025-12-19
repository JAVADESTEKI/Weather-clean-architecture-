// di/AppModule.kt
package ir.example1.weather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.example1.weather.BuildConfig
import ir.example1.weather.data.remote.api.ApiClient
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.data.repository.WeatherRepositoryImpl
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApiServices(apiClient: ApiClient): ApiServices = apiClient.apiServices

    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.WEATHER_API_KEY

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: ApiServices,
        weatherMapper: WeatherMapper,
        forecastMapper: ForecastMapper,
        cityMapper: CityMapper,
        apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            apiService = apiService,
            weatherMapper = weatherMapper,
            forecastMapper = forecastMapper,
            cityMapper = cityMapper,
            apiKey = apiKey
        )
    }
}
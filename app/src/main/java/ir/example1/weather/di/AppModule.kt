package ir.example1.weather.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.example1.weather.BuildConfig
import ir.example1.weather.data.local.dao.ForecastDao
import ir.example1.weather.data.local.dao.WeatherDao
import ir.example1.weather.data.local.database.WeatherDatabase
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

    // ---------- API ----------
    @Provides
    @Singleton
    fun provideApiServices(apiClient: ApiClient): ApiServices =
        apiClient.apiServices

    @Provides
    @Singleton
    fun provideApiKey(): String =
        BuildConfig.WEATHER_API_KEY

    // ---------- ROOM ----------
    @Provides
    @Singleton
    fun provideDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(
            app,
            WeatherDatabase::class.java,
            "weather_db"
        ).build()
    }

    @Provides
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao =
        db.weatherDao()

    @Provides
    fun provideForecastDao(db: WeatherDatabase): ForecastDao =
        db.forecastDao()

    // ---------- REPOSITORY ----------
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: ApiServices,
        weatherMapper: WeatherMapper,
        forecastMapper: ForecastMapper,
        cityMapper: CityMapper,
        weatherDao: WeatherDao,       // ✅ اضافه شد
        forecastDao: ForecastDao,     // ✅ اضافه شد
        apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            apiService = apiService,
            weatherMapper = weatherMapper,
            forecastMapper = forecastMapper,
            cityMapper = cityMapper,
            weatherDao = weatherDao,           // ✅
            forecastDao = forecastDao,         // ✅
            apiKey = apiKey
        )
    }
}

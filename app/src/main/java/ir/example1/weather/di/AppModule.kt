package ir.example1.weather.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.example1.weather.BuildConfig

import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.database.WeatherDatabase
import ir.example1.weather.data.remote.api.ApiClient
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.data.repository.WeatherRepositoryImpl
import ir.example1.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideApiServices(apiClient: ApiClient): ApiServices = apiClient.apiServices

    @Provides @Singleton
    fun provideApiKey(): String = BuildConfig.WEATHER_API_KEY

    @Provides @Singleton
    fun provideDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(app, WeatherDatabase::class.java, "weather_db")
            .fallbackToDestructiveMigration()
            .build()
    }

//    @Provides fun provideWeatherDao(db: WeatherDatabase): WeatherDao = db.weatherDao()
//    @Provides fun provideForecastDao(db: WeatherDatabase): ForecastDao = db.forecastDao()
    @Provides fun provideCityDao(db: WeatherDatabase): CityDao = db.cityDao()

    @Provides @Singleton
    fun provideWeatherRepository(
        apiService: ApiServices,
        weatherMapper: WeatherMapper,
        forecastMapper: ForecastMapper,
        cityMapper: CityMapper,
        cityDao: CityDao,
        apiKey: String,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            apiService = apiService,
            weatherMapper = weatherMapper,
            forecastMapper = forecastMapper,
            cityMapper = cityMapper,
            cityDao = cityDao,
            apiKey = apiKey,
            ioDispatcher = ioDispatcher
        )
    }
}

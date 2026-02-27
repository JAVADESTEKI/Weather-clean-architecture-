package ir.example1.weather.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.example1.weather.data.fake.FakeApiService
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.database.WeatherDatabase
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.res.util.TestDataFactory
import ir.example1.weather.util.MainDispatcherRule
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class WeatherRepositoryRoomIntegrationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var database: WeatherDatabase
    private lateinit var cityDao: CityDao
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        cityDao = database.cityDao()

        repository = WeatherRepositoryImpl(
            weatherMapper = WeatherMapper(),
            forecastMapper = ForecastMapper(),
            apiService = FakeApiService(), // ساده
            cityMapper = CityMapper(),
            cityDao = cityDao,
            apiKey = "test_key",
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun deleteCity_removesWeatherAndForecasts_viaCascade() = runTest{

        val city = TestDataFactory.cityEntity()
        val weather = TestDataFactory.weatherEntity(cityId = city.id)
        val forecast = TestDataFactory.forecastEntity(cityId = city.id)

        cityDao.insertCity(city)
        cityDao.insertWeather(weather)
        cityDao.insertForecasts(listOf(forecast))

        cityDao.deleteCityById(city.id)

        val fullData = cityDao.getLastSelectedCityFullData(city.id)

        assertNull(fullData)
    }
}


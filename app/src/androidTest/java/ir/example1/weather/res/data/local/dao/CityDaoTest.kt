package ir.example1.weather.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.example1.weather.data.local.database.WeatherDatabase
import ir.example1.weather.data.mapper.toEntity
import ir.example1.weather.res.util.TestDataFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CityDaoTest {

    private lateinit var db: WeatherDatabase
    private lateinit var cityDao: CityDao

    @Before
    fun setup() {
        // ساخت دیتابیس in-memory
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        cityDao = db.cityDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertCity_should_save_city() = runBlocking {
        val cityEntity = TestDataFactory.cityEntity()
        val id = cityDao.insertCity(cityEntity)

        val cities = cityDao.getAllCities()
        assertEquals(1, cities.size)
        assertEquals(id, cities[0].id)
        assertEquals(cityEntity.name, cities[0].name)
    }

    @Test
    fun deleteCityById_should_remove_city() = runBlocking {
        val cityEntity = TestDataFactory.cityEntity()
        val cityId = cityDao.insertCity(cityEntity)

        cityDao.deleteCityById(cityId)

        val cities = cityDao.getAllCities()
        assertTrue(cities.isEmpty())
    }

    @Test
    fun updateWeatherForecasts_should_update_correctly() = runBlocking {
        val cityEntity = TestDataFactory.cityEntity()
        val cityId = cityDao.insertCity(cityEntity)

        val weather = TestDataFactory.weatherDomain().toEntity().copy(cityId = cityId)
        val forecast = TestDataFactory.forecastDomain().toEntity().copy(cityId = cityId)

        cityDao.updateWeatherForecasts(cityId, weather, listOf(forecast))

        val savedCityData = cityDao.getLastSelectedCityFullData(cityId)
        assertEquals(cityId, savedCityData?.city?.id)
        assertEquals(weather.cityId, savedCityData?.weather?.cityId)
        assertEquals(1, savedCityData?.forecasts?.size)
    }

    @Test
    fun getLastSelectedCity_should_return_correct_city() = runBlocking {
        val cityEntity = TestDataFactory.cityEntity()
        val cityId = cityDao.insertCity(cityEntity)

        val lastCity = cityDao.getLastSelectedCity(cityId)
        assertEquals(cityId, lastCity?.id)
        assertEquals(cityEntity.name, lastCity?.name)
    }
}
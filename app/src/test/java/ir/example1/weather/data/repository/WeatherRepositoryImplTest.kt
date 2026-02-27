package ir.example1.weather.data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.util.TestDataFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import ir.example1.weather.data.fake.FakeCityDao
import ir.example1.weather.data.remote.response.CityResponse
import ir.example1.weather.util.MainDispatcherRule
import junit.framework.TestCase.assertTrue
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: WeatherRepositoryImpl

    private val weatherMapper: WeatherMapper = mockk()
    private val forecastMapper: ForecastMapper = mockk()
    private val cityMapper: CityMapper = mockk()
    private val apiService: ApiServices = mockk()
    private lateinit var cityDao: FakeCityDao

    private val apiKey = "test_key"

    @Before
    fun setup() {
        cityDao = FakeCityDao()

        repository = WeatherRepositoryImpl(
            weatherMapper,
            forecastMapper,
            apiService,
            cityMapper,
            cityDao,
            apiKey,
            mainDispatcherRule.dispatcher
        )
    }

    @Test
    fun `getSavedCities returns mapped cities`() = runTest {
        cityDao.insertCity(TestDataFactory.cityEntity())

        val result = repository.getSavedCities()

        assertThat(result).hasSize(1)
        assertEquals(result.first().id, 1L)
    }

    @Test
    fun `deleteCity removes city`() = runTest {
        val id = cityDao.insertCity(TestDataFactory.cityEntity())

        repository.deleteCity(id)

        assertThat(cityDao.getAllCities()).isEmpty()
    }

    @Test
    fun deleteCity_removesCityAndRelatedData() = runTest {
        val city = TestDataFactory.cityEntity()
        val weather = TestDataFactory.weatherEntity().copy(cityId = city.id)
        val forecast = TestDataFactory.forecastEntity().copy(cityId = city.id)

        cityDao.insertCity(city)
        cityDao.insertWeather(weather)
        cityDao.insertForecasts(listOf(forecast))

        cityDao.deleteCityById(city.id)

        assertTrue(cityDao.getAllCities().isEmpty())
    }

    @Test
    fun `saveCityFullData inserts related data correctly`() = runTest {

        val city = TestDataFactory.cityDomain()
        val weather = TestDataFactory.weatherDomain()
        val forecasts = listOf(TestDataFactory.forecastDomain())

        val id = repository.saveCityFullData(city, weather, forecasts)

        assertThat(cityDao.insertedWeather?.cityId).isEqualTo(id)
        assertThat(cityDao.insertedForecasts.first().cityId).isEqualTo(id)
    }


    @Test
    fun `searchCities success returnsMappedCities`() = runTest {

        val fakeResponse = mockk<CityResponse>()
        val mappedCities = listOf(TestDataFactory.cityDomain())

        coEvery {
            apiService.getCitiesList("Teh", 5, apiKey)
        } returns fakeResponse

        coEvery {
            cityMapper.mapToCityList(fakeResponse)
        } returns mappedCities

        val result = repository.searchCities("Teh", 5)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)

        coVerify { apiService.getCitiesList("Teh", 5, apiKey) }
        coVerify { cityMapper.mapToCityList(fakeResponse) }
    }

    @Test
    fun `searchCities failure returnsFailure`() = runTest {

        val exception = RuntimeException("Network error")

        coEvery {
            apiService.getCitiesList(any(), any(), apiKey)
        } throws exception

        val result = repository.searchCities("Teh", 5)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        coVerify(exactly = 0) {
            cityMapper.mapToCityList(any())
        }
    }

    @Test
    fun `getLastSelectedCityFullData returnsMappedData`() = runTest {

        val fakeEntity = TestDataFactory.cityFullDAtaEntity()
        cityDao.fakeFullData = fakeEntity

        val result = repository.getLastSelectedCityFullData(1L)

        assertThat(result).isNotNull()
        assertThat(result?.city?.id).isEqualTo(fakeEntity.city.id)
    }

    @Test
    fun insertWeather_throwsException_whenCityDoesNotExist() = runTest {

        val weather = TestDataFactory.weatherEntity().copy(cityId = 999L)

        assertFailsWith<IllegalStateException> {
            cityDao.insertWeather(weather)
        }
    }
}
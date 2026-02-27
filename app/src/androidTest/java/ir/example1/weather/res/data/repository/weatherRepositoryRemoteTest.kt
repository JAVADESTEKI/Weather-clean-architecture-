package ir.example1.weather.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.example1.weather.data.fake.FakeCityDao
import ir.example1.weather.data.remote.api.ApiServices
import ir.example1.weather.data.remote.mapper.CityMapper
import ir.example1.weather.data.remote.mapper.ForecastMapper
import ir.example1.weather.data.remote.mapper.WeatherMapper
import ir.example1.weather.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class WeatherRepositoryRemoteTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: WeatherRepositoryImpl

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServices::class.java)

        repository = WeatherRepositoryImpl(
            weatherMapper = WeatherMapper(),
            forecastMapper = ForecastMapper(),
            apiService = apiService,
            cityMapper = CityMapper(),
            cityDao = FakeCityDao(),
            apiKey = "94e31d0a49763138889063f6c2d3b6f7",
            ioDispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // ---------------- SUCCESS TEST ----------------

    @Test
    fun getCurrentWea_returnsMappedWeather_whenResponseIsSuccessful() = runTest {

        val fakeJson = """
           {
              "coord": {
                "lon": 13.41,
                "lat": 52.52
              },
              "weather": [
                {
                  "id": 501,
                  "main": "Rain",
                  "description": "moderate rain",
                  "icon": "10d"
                }
              ],
              "base": "stations",
              "main": {
                "temp": 298.48,
                "feels_like": 298.74,
                "temp_min": 297.56,
                "temp_max": 300.05,
                "pressure": 1015,
                "humidity": 64,
                "sea_level": 1015,
                "grnd_level": 933
              },
              "visibility": 10000,
              "wind": {
                "speed": 0.62,
                "deg": 349,
                "gust": 1.18
              },
              "rain": {
                "1h": 3.16
              },
              "clouds": {
                "all": 100
              },
              "dt": 1661870592,
              "sys": {
                "type": 2,
                "id": 2075663,
                "country": "IT",
                "sunrise": 1661834187,
                "sunset": 1661882248
              },
              "timezone": 7200,
              "id": 3163858,
              "name": "Berlin",
              "cod": 200 
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(fakeJson)
        )

        val result = repository.getCurrentWea(
            lat = 52.52,
            lon = 13.41,
            name = "Berlin",
            unit = "metric"
        )

        advanceUntilIdle()

        assertTrue(result.isSuccess)

        val weather = result.getOrNull()
        assertEquals("Berlin", weather?.cityName)

        // verify request
        val request: RecordedRequest = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("lat=52.52"))
        assertTrue(request.path!!.contains("lon=13.41"))
        assertTrue(request.path!!.contains("appid=94e31d0a49763138889063f6c2d3b6f7"))
    }

    // ---------------- ERROR TEST ----------------

    @Test
    fun getCurrentWea_returnsFailure_whenResponseIs404() = runTest {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val result = repository.getCurrentWea(
            lat = 0.0,
            lon = 0.0,
            name = "Nowhere",
            unit = "metric"
        )

        advanceUntilIdle()

        assertTrue(result.isFailure)
    }
}
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@RunWith(AndroidJUnit4::class)
class WeatherRepositoryRemoteTest2 {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: WeatherRepositoryImpl

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // استفاده از port به جای getPort
        val port = mockWebServer.port
        val baseUrl = "http://127.0.0.1:$port/"
        println("=== MOCK WEB SERVER URL: $baseUrl ===")

        // ایجاد TrustManager که همه گواهی‌ها را قبول کند
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // ایجاد SSLContext که همه چیز را قبول کند
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // ایجاد OkHttpClient ناامن
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
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

    @OptIn(ExperimentalCoroutinesApi::class)
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

        println("Before calling repository")
        val result = repository.getCurrentWea(
            lat = 52.52,
            lon = 13.41,
            name = "Berlin",
            unit = "metric"
        )
        println("After calling repository")

        advanceUntilIdle()

        if (result.isFailure) {
            println("=== ERROR DETAILS ===")
            println("Error: ${result.exceptionOrNull()}")
            println("Error message: ${result.exceptionOrNull()?.message}")
            result.exceptionOrNull()?.printStackTrace()
        }

        assertTrue("Result should be success. Error: ${result.exceptionOrNull()?.message}",
            result.isSuccess)

        val weather = result.getOrNull()
        assertEquals("Berlin", weather?.cityName)

        val request: RecordedRequest = mockWebServer.takeRequest()
        println("Request path: ${request.path}")
        assertTrue(request.path!!.contains("lat=52.52"))
        assertTrue(request.path!!.contains("lon=13.41"))
        assertTrue(request.path!!.contains("appid=94e31d0a49763138889063f6c2d3b6f7"))
    }

}

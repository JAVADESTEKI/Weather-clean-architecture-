package ir.example1.weather.domain.usecase
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetSavedCitiesUseCaseTest2 {

    @Test
    fun `invoke should return cities from repository`() = runTest {

        val repository = mockk<WeatherRepository>()

        val fakeCities = listOf(
            City(
                id = 1,
                name = "Tehran",
                country = "IR",
                lat = 50.0,
                lon = 51.0,
                selectedAt = 13454652424,
                localName = "teh"
            ),
            City(id = 2,
                name = "Shiraz",
                country = "IR",
                lat = 80.0,
                lon = 20.0,
                selectedAt = 1123546123124,
                localName = "shiraaz")
        )

        coEvery { repository.getSavedCities() } returns fakeCities

        val useCase = GetSavedCitiesUseCase(repository)

        val result = useCase()

        assertThat(result).isEqualTo(fakeCities)
        coVerify(exactly = 1) { repository.getSavedCities() }
    }
}

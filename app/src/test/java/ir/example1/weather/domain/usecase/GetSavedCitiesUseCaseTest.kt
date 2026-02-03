package ir.example1.weather.domain.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetSavedCitiesUseCaseTest {

    @Test
    fun `invoke should return saved cities from repository`() = runTest {

        val fakeRepository = FakeWeatherRepository()
        val useCase = GetSavedCitiesUseCase(fakeRepository)


        val result = useCase()


        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Tehran")
        assertThat(result[1].name).isEqualTo("Shiraz")
    }
}

package ir.example1.weather.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.usecase.SearchCitiesUseCase
import ir.example1.weather.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CitySearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `searchCities with short query resets state`() = runTest {

        val searchUseCase = mockk<SearchCitiesUseCase>(relaxed = true)
        val viewModel = CitySearchViewModel(searchUseCase)


        viewModel.searchCities("a") // length < 2


        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.cities).isEmpty()
        assertThat(state.error).isNull()


        coVerify(exactly = 0) { searchUseCase(any<String>()) }
    }
    //-------------------------------------------------------------------------------------
    @Test
    fun `searchCities success updates state with cities`() = runTest {

        val cities = listOf(
            City(
                id = 1,
                name = "Tehran",
                country = "IR",
                lat = 50.0,
                lon = 51.0,
                selectetAt = 13454652424,
                localName = "tehran"
            )
        )

        val searchUseCase = mockk<SearchCitiesUseCase>()
        coEvery { searchUseCase("teh") } returns Result.success(cities)

        val viewModel = CitySearchViewModel(searchUseCase)


        viewModel.searchCities("teh")

        runCurrent() //  coroutine start

        assertThat(viewModel.uiState.value.isLoading).isTrue()


        advanceTimeBy(300)


        runCurrent() //  coroutine continue


        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.cities).isEqualTo(cities)
        assertThat(state.error).isNull()

        coVerify(exactly = 1) { searchUseCase("teh") }
    }
    //-------------------------------------------------------------------------------------
    @Test
    fun `searchCities failure updates state with error`() = runTest {

        val exception = RuntimeException("Network error")

        val searchUseCase = mockk<SearchCitiesUseCase>()
        coEvery { searchUseCase("teh") } returns Result.failure(exception)

        val viewModel = CitySearchViewModel(searchUseCase)

        viewModel.searchCities("teh")

        runCurrent()


        assertThat(viewModel.uiState.value.isLoading).isTrue()

        advanceTimeBy(300)
        runCurrent()


        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.cities).isEmpty()
        assertThat(state.error).contains("Network")

        coVerify(exactly = 1) { searchUseCase("teh") }
    }
    //-------------------------------------------------------------------------------------
    @Test
    fun `new search cancels previous one`() = runTest {

        val firstCities = listOf(
            City(
                id = 1,
                name = "Tehran",
                country = "IR",
                lat = 50.0,
                lon = 51.0,
                selectetAt = 13454652424,
                localName = "tehran"
            )
        )
        val secondCities = listOf(
            City(
                id = 2,
                name = "Tabriz",
                country = "IR",
                lat = 20.0,
                lon = 30.0,
                selectetAt = 13454652454,
                localName = "tabriz"
            )
        )

        val searchUseCase = mockk<SearchCitiesUseCase>()

        coEvery { searchUseCase("te") } coAnswers {
            delay(300)
            Result.success(firstCities)
        }

        coEvery { searchUseCase("teh") } returns Result.success(secondCities)

        val viewModel = CitySearchViewModel(searchUseCase)


        viewModel.searchCities("te")
        runCurrent()

        viewModel.searchCities("teh")
        runCurrent()

        advanceTimeBy(300)
        runCurrent()


        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.cities).isEqualTo(secondCities)


        coVerify(exactly = 1) { searchUseCase("teh", 10) }
        coVerify(exactly = 0) { searchUseCase("te", 10) } // searchUseCase("te") should not executed
    }
    //-------------------------------------------------------------------------------------
    @Test
    fun `clearError resets only error field`() = runTest {

        val exception = RuntimeException("Network error")

        val searchUseCase = mockk<SearchCitiesUseCase>()
        coEvery { searchUseCase("teh") } returns Result.failure(exception)

        val viewModel = CitySearchViewModel(searchUseCase)

        viewModel.searchCities("teh")

        runCurrent()
        advanceTimeBy(300)
        runCurrent()

        assertThat(viewModel.uiState.value.error).isNotNull()

        viewModel.clearError()

        val state = viewModel.uiState.value

        assertThat(state.error).isNull()
        assertThat(state.cities).isEmpty()
        assertThat(state.isLoading).isFalse()
    }
    //-------------------------------------------------------------------------------------
    @Test
    fun `searchCitiesLatLon success updates state correctly`() = runTest {

        val cities = listOf(
            City(
                id = 2,
                name = "Tabriz",
                country = "IR",
                lat = 10.0,
                lon = 20.0,
                selectetAt = 0,
                localName = "Tabriz"
            )
        )

        val searchUseCase = mockk<SearchCitiesUseCase>()

        coEvery { searchUseCase(10.0, 20.0) } returns Result.success(cities)

        val viewModel = CitySearchViewModel(searchUseCase)

        viewModel.searchCitiesLatLon(10.0, 20.0)

        runCurrent()

        assertThat(viewModel.uiState.value.isLoading).isTrue()

        advanceTimeBy(300)
        runCurrent()

        val state = viewModel.uiState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.cities).isEqualTo(cities)
        assertThat(state.error).isNull()

        coVerify(exactly = 1) { searchUseCase(10.0, 20.0) }
    }
}

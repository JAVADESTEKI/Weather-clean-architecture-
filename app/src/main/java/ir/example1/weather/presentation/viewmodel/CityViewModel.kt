package ir.example1.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.usecase.GetCurrentWeatherUseCase
import ir.example1.weather.domain.usecase.GetForecastUseCase
import ir.example1.weather.domain.usecase.SaveCityFullDataUseCase
import ir.example1.weather.domain.usecase.SaveLastSelectedCityIdUseCase
import ir.example1.weather.domain.usecase.SearchCitiesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val saveCityFullDataUseCase: SaveCityFullDataUseCase,
    private val saveLastSelectedCityIdUseCase: SaveLastSelectedCityIdUseCase


    ) : ViewModel() {

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: Job? = null

    private fun performSearch(
        searchBlock: suspend () -> Result<List<City>>
    ) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _loading.value = true
            _error.value = null
            delay(300)

            searchBlock().fold(
                onSuccess = { _cities.value = it },
                onFailure = { _cities.value = emptyList() }
            )

            _loading.value = false
        }
    }
    fun searchCities(query: String) {
        if (query.length < 2) {
            _cities.value = emptyList()
            return
        }

        performSearch {
            searchCitiesUseCase(query)
        }
    }

    fun searchCitiesLatLon(lat: Double, lon: Double) {
        performSearch {
            searchCitiesUseCase(lat, lon)
        }
    }



    fun clearError() {
        _error.value = null
    }
}
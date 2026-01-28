package ir.example1.weather.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.usecase.DeleteCityUseCase
import ir.example1.weather.domain.usecase.GetCurrentWeatherUseCase
import ir.example1.weather.domain.usecase.GetForecastUseCase
import ir.example1.weather.domain.usecase.GetLastInsertedIdUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityFullDataUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityIdUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityUseCase
import ir.example1.weather.domain.usecase.GetSavedCitiesUseCase
import ir.example1.weather.domain.usecase.SaveCityFullDataUseCase
import ir.example1.weather.domain.usecase.SaveLastSelectedCityIdUseCase
import ir.example1.weather.domain.usecase.UpdateCityFullDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val saveCityFullDataUseCase: SaveCityFullDataUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val getLastSelectedCityIdUseCase: GetLastSelectedCityIdUseCase,
    private val saveLastSelectedCityIdUseCase: SaveLastSelectedCityIdUseCase,
    private val getLastInsertedIdUseCase: GetLastInsertedIdUseCase,
    private val updateCityFullDataUseCase: UpdateCityFullDataUseCase,
    private val getLastSelectedCityFullDataUseCase: GetLastSelectedCityFullDataUseCase,
    private val getLastSelectedCityUseCase: GetLastSelectedCityUseCase
) : ViewModel() {


    val _uiState = MutableStateFlow(WeatherUiState())
    val uiState = _uiState.asStateFlow()

    fun loadInitialWeather() {
        viewModelScope.launch {
            getLastSelectedCityIdUseCase()
                .filterNotNull()
                .collect { cityId: Long ->
                    val city = getLastSelectedCityFullDataUseCase(cityId)
                    loadWeatherData(city)
                }
        }
    }


    fun refreshWeather() {
        viewModelScope.launch {
            val cityId = getLastSelectedCityIdUseCase()
                .filterNotNull()
                .first()

            val city = getLastSelectedCityUseCase(cityId) ?: return@launch
            _uiState.update { it.copy(isLoading = true, error = null) }

            val weatherResult = getCurrentWeatherUseCase(city.lat, city.lon, city.name)
            val forecastResult = getForecastUseCase(city.lat, city.lon)

            if (weatherResult.isFailure || forecastResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to receive weather data!"
                    )
                }

                return@launch
            }

            val weather = weatherResult.getOrNull()!!
            val forecast = forecastResult.getOrNull()!!

            updateCityFullDataUseCase(cityId, weather, forecast)

            val updatedCity = getLastSelectedCityFullDataUseCase(cityId)
            loadWeatherData(updatedCity)


        }
    }

    fun loadWeatherData(city: CityWeatherForecast?) {

        if (city == null || city.weather == null || city.forecasts == null) return

        _uiState.update {
            it.copy(
                isLoading = false,
                currentWeather = city.weather,
                forecast = city.forecasts,
                error = null
            )
        }
    }

    fun saveSelectedCity(city: City) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val weatherResult = getCurrentWeatherUseCase(city.lat, city.lon, city.name)
            val forecastResult = getForecastUseCase(city.lat, city.lon)

            if (weatherResult.isFailure || forecastResult.isFailure){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to receive weather data!"
                    )
                    return@launch
                }
            }
                val cityId = saveCityFullDataUseCase(
                    city,
                    weatherResult.getOrNull()!!,
                    forecastResult.getOrNull()!!
                )
                saveLastSelectedCityIdUseCase(cityId)
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
        }
    }

    fun loadSavedCities() {
        viewModelScope.launch {
            val cities = getSavedCitiesUseCase()
            _uiState.update { it.copy(savedCities = cities) }
//            Log.d("STATE", "Saved cities = ${uiState.value.savedCities.size}")
        }
    }

    fun selectCity(cityId: Long?) {
        if (cityId == null) return
        viewModelScope.launch {
            saveLastSelectedCityIdUseCase(cityId)
        }
    }

    fun deleteCity(cityId: Long?) {
        if (cityId == null) return

        viewModelScope.launch {
            deleteCityUseCase(cityId)

            val cities = getSavedCitiesUseCase()
            val newId = getLastInsertedIdUseCase()

            saveLastSelectedCityIdUseCase(newId)
            _uiState.update { it.copy(savedCities = cities) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
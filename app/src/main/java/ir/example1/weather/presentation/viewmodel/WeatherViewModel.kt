package ir.example1.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.data.local.dao.CityDao
import ir.example1.weather.data.local.dto.CityFullData
import ir.example1.weather.data.mapper.toDomain
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.usecase.GetCurrentWeatherUseCase
import ir.example1.weather.domain.usecase.GetForecastUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityUseCase
import ir.example1.weather.domain.usecase.SaveCityFullDataUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getLastSelectedCityUseCase: GetLastSelectedCityUseCase,
    private val SaveCityFullDataUseCase: SaveCityFullDataUseCase,
    private val GetCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val GetForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<Weather?>(null)
    val currentWeather: StateFlow<Weather?> = _currentWeather.asStateFlow()

    private val _forecast = MutableStateFlow<List<Forecast>>(emptyList())
    val forecast: StateFlow<List<Forecast>> = _forecast.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadInitialWeather() {

        viewModelScope.launch {
            delay(2000)
            val city= getLastSelectedCityUseCase()
            if (city != null) {
                loadWeatherData(city)
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val city = getLastSelectedCityUseCase()
            if (city != null)
                loadWeatherData(city)

        }
    }

    fun loadWeatherData(city: CityFullData) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            _currentWeather.value= city.weather.toDomain()
//            _currentWeather.value?.cityName= city.city.name
            _forecast.value= city.forecasts.map{it.toDomain() }

            _loading.value = false
        }
    }

    fun saveSelectedCity(city: City) {
        viewModelScope.launch {
            val result1= GetCurrentWeatherUseCase(city.lat, city.lon, city.name)
            val result2= GetForecastUseCase(city.lat, city.lon)


            val weather: Weather = result1.getOrNull()!!
            val forecast: List<Forecast> = result2.getOrNull()!!

            SaveCityFullDataUseCase(city,weather, forecast)
        }
    }
    fun clearError() {
        _error.value = null
    }
}

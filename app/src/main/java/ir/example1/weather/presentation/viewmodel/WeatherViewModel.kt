// presentation/viewmodel/WeatherViewModel.kt
package ir.example1.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.usecase.GetCurrentWeatherUseCase
import ir.example1.weather.domain.usecase.GetForecastUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<Weather?>(null)
    val currentWeather: StateFlow<Weather?> = _currentWeather.asStateFlow()

    private val _forecast = MutableStateFlow<List<Forecast>>(emptyList())
    val forecast: StateFlow<List<Forecast>> = _forecast.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadWeatherData(lat: Double, lon: Double, name: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            getCurrentWeatherUseCase(lat, lon, name).fold(
                onSuccess = { weather ->
                    _currentWeather.value = weather
                },
                onFailure = { throwable ->
                    _error.value = "خطا در دریافت اطلاعات آب‌وهوا: ${throwable.message}"
                }
            )

            getForecastUseCase(lat, lon).fold(
                onSuccess = { forecastList ->
                    _forecast.value = forecastList
                },
                onFailure = { throwable ->
                    _error.value = "خطا در دریافت پیش‌بینی: ${throwable.message}"
                }
            )

            _loading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
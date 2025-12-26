
package ir.example1.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getLastSelectedCityUseCase: GetLastSelectedCityUseCase,
    private val saveSelectedCityUseCase: SaveSelectedCityUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val deleteSavedCityUseCase: DeleteSavedCityUseCase
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<Weather?>(null)
    val currentWeather: StateFlow<Weather?> = _currentWeather.asStateFlow()

    private val _forecast = MutableStateFlow<List<Forecast>>(emptyList())
    val forecast: StateFlow<List<Forecast>> = _forecast.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _savedCities = MutableStateFlow<List<City>>(emptyList())
    val savedCities: StateFlow<List<City>> = _savedCities.asStateFlow()

    fun loadInitialWeather() {
        viewModelScope.launch {
            val city = getLastSelectedCityUseCase()
            if (city != null) {
                loadWeatherData(city.lat, city.lon, city.name, forceRefresh = false)
            } else {
                loadWeatherData(51.50, -0.12, "London", forceRefresh = false)
            }
        }
    }

    fun loadWeatherData(lat: Double, lon: Double, name: String, forceRefresh: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            getCurrentWeatherUseCase(lat, lon, name, forceRefresh = forceRefresh).fold(
                onSuccess = { weather -> _currentWeather.value = weather },
                onFailure = { throwable -> _error.value = "خطا در دریافت اطلاعات آب‌وهوا: ${throwable.message}" }
            )

            getForecastUseCase(lat, lon, forceRefresh = forceRefresh).fold(
                onSuccess = { list -> _forecast.value = list },
                onFailure = { throwable -> _error.value = "خطا در دریافت پیش‌بینی: ${throwable.message}" }
            )

            _loading.value = false
        }
    }

    fun loadSavedCities() {
        viewModelScope.launch {
            _savedCities.value = getSavedCitiesUseCase()
        }
    }

    fun deleteSavedCity(city: City) {
        viewModelScope.launch {
            deleteSavedCityUseCase(city)
            _savedCities.value = getSavedCitiesUseCase()
        }
    }

    fun selectSavedCity(city: City) {
        viewModelScope.launch {
            // به‌عنوان آخرین شهر ذخیره شود
            saveSelectedCityUseCase(city)
            // برای این شهر بلافاصله داده‌ها را لود کن (ترجیحا فورس رفرش)
            loadWeatherData(city.lat, city.lon, city.name, forceRefresh = true)
        }
    }

    fun clearError() { _error.value = null }
}

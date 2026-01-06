package ir.example1.weather.presentation.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.usecase.DeleteCityUseCase
import ir.example1.weather.domain.usecase.GetSavedCitiesUseCase
import ir.example1.weather.domain.usecase.GetCurrentWeatherUseCase
import ir.example1.weather.domain.usecase.GetForecastUseCase
import ir.example1.weather.domain.usecase.GetLastInsertedIdUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityFullDataUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityIdUseCase
import ir.example1.weather.domain.usecase.GetLastSelectedCityUseCase
import ir.example1.weather.domain.usecase.SaveCityFullDataUseCase
import ir.example1.weather.domain.usecase.SaveLastSelectedCityIdUseCase
import ir.example1.weather.domain.usecase.UpdateCityFullDataUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val saveCityFullDataUseCase: SaveCityFullDataUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val deleteCityUseCase:DeleteCityUseCase,
    private val getLastSelectedCityIdUseCase: GetLastSelectedCityIdUseCase,
    private val saveLastSelectedCityIdUseCase: SaveLastSelectedCityIdUseCase,
    private val getLastInsertedIdUseCase: GetLastInsertedIdUseCase,
    private val updateCityFullDataUseCase: UpdateCityFullDataUseCase,
    private val getLastSelectedCityFullDataUseCase: GetLastSelectedCityFullDataUseCase,
    private val getLastSelectedCityUseCase:GetLastSelectedCityUseCase
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
    val savedCities = _savedCities.asStateFlow()

    fun loadInitialWeather() {
        viewModelScope.launch {
            getLastSelectedCityIdUseCase()
                .filterNotNull()
                .collect { cityId: Long ->
                    val city= getLastSelectedCityFullDataUseCase(cityId)
                    loadWeatherData(city)
                }
        }
    }


    fun refreshWeather() {
        viewModelScope.launch {
            val cityId= getLastSelectedCityIdUseCase()
            .filterNotNull()
            .first()

            if(cityId!= null) {
                val city = getLastSelectedCityUseCase(cityId)
                if (city != null) {
                    var weather: Weather? = null
                    var forecast: List<Forecast>? = null

                    getCurrentWeatherUseCase(city.lat, city.lon, city.name).fold(
                        onSuccess = { it ->
                            weather = it
                        },
                        onFailure = { error ->
                        }
                    )
                    getForecastUseCase(city.lat, city.lon).fold(
                        onSuccess = { it ->
                            forecast = it
                        },
                        onFailure = { error ->
                        }
                    )
                    if (weather == null || forecast == null)
                        _error.value = "Failed to recieve weather data!"
                    else {
                        updateCityFullDataUseCase(cityId, weather, forecast)
                        val cityTemp = getLastSelectedCityFullDataUseCase(cityId)
                        loadWeatherData(cityTemp)
                    }
                }

            }
        }
    }

    fun loadWeatherData(city: CityWeatherForecast?) {

        if(city!=null&& city.weather!=null && city.forecasts!=null)
            viewModelScope.launch {
                _loading.value = true
                _error.value = null

                _currentWeather.value= city.weather
                _forecast.value= city.forecasts.map{it }

                _loading.value = false
            }
    }

    fun clearError() {
        _error.value = null
    }
    fun saveSelectedCity(city: City) {
        viewModelScope.launch {
            var weather: Weather? = null
            var forecast: List<Forecast>? = null

            getCurrentWeatherUseCase(city.lat, city.lon, city.name).fold(
                onSuccess = { it ->
                    weather = it
                },
                onFailure = { error ->
                    _error.value = "Failed to recieve weather data!"
                }
            )
            getForecastUseCase(city.lat, city.lon).fold(
                onSuccess = { it ->
                    forecast = it
                },
                onFailure = { error ->
                    _error.value = "Failed to recieve weather data!"
                }
            )
            if (weather != null && forecast != null) {
                val cityId=saveCityFullDataUseCase(city,weather, forecast)
                saveLastSelectedCityIdUseCase(cityId)
            }
        }
    }

    fun loadSavedCities() {
        viewModelScope.launch {
            _savedCities.value = getSavedCitiesUseCase()
        }
    }

    fun selectCity(cityId:Long?){
        if(cityId!=null)
            viewModelScope.launch {
                saveLastSelectedCityIdUseCase(cityId)
            }
    }

    fun deleteCity(cityId:Long?){
        viewModelScope.launch {
            deleteCityUseCase(cityId)
            loadSavedCities()

            val cityId= getLastInsertedIdUseCase()
            saveLastSelectedCityIdUseCase(cityId)
        }
    }
}
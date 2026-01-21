package ir.example1.weather.presentation.viewmodel

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather

data class WeatherUiState(
    val isLoading: Boolean = false,
    val currentWeather: Weather? = null,
    val forecast: List<Forecast> = emptyList(),
    val savedCities: List<City> = emptyList(),
    val error: String? = null
)


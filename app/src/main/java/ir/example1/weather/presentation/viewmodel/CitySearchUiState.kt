package ir.example1.weather.presentation.viewmodel

import ir.example1.weather.domain.model.City

data class CitySearchUiState(
    val isLoading: Boolean = false,
    val cities: List<City> = emptyList(),
    val error: String? = null
)
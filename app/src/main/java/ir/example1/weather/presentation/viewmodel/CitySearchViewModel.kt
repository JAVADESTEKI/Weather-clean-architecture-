package ir.example1.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.usecase.SearchCitiesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySearchUiState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null

    private fun performSearch(
        searchBlock: suspend () -> Result<List<City>>
    ) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            delay(300)

            val result = searchBlock()

            result.fold(
                onSuccess = { cities ->
                    _uiState.update {
                        CitySearchUiState(
                            isLoading = false,
                            cities = cities
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        CitySearchUiState(
                            isLoading = false,
                            cities = emptyList(),
                            error = throwable.message ?: "Search failed"
                        )
                    }
                }
            )
        }
    }

    fun searchCities(query: String) {
        if (query.length < 2) {
            _uiState.update { CitySearchUiState() }
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
        _uiState.update { it.copy(error = null) }
    }
}
// domain/usecase/SearchCitiesUseCase.kt
package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        query: String,
        limit: Int = 10
    ): Result<List<City>> {
        return repository.searchCities(query, limit)
    }

    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        limit: Int=10
    ): Result<List<City>> {
        return repository.searchCitiesReverse(lat, lon, limit)
    }
}
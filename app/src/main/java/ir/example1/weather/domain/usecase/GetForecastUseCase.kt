package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        unit: String = "metric",
        forceRefresh: Boolean = false
    ): Result<List<Forecast>> {
        return repository.getForecast(lat, lon, unit, forceRefresh)
    }
}

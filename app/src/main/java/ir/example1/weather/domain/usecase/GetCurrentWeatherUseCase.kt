package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        lat: Double,
        lon: Double,
        name: String,
        unit: String = "metric",
        forceRefresh: Boolean = false
    ): Result<Weather> {
        return repository.getCurrentWeather(lat, lon, name, unit, forceRefresh)
    }
}

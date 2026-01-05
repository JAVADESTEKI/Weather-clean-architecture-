package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class UpdateCityFullDataUseCase @Inject constructor(
    private val repository: WeatherRepository
)  {
    suspend operator fun invoke(cityId: Long, weather: Weather, forecasts: List<Forecast>) =
        repository.updateCityFullData(cityId, weather, forecasts)
}
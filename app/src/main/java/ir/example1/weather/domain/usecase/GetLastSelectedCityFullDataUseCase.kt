package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.CityWeatherForecast
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetLastSelectedCityFullDataUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityId:Long): CityWeatherForecast? = repository.getLastSelectedCityFullData(cityId)
}
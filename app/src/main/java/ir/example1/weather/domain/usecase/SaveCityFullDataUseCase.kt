package ir.example1.weather.domain.usecase

import ir.example1.weather.data.local.dto.CityFullData
import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.model.Forecast
import ir.example1.weather.domain.model.Weather
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveCityFullDataUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: City, weather: Weather, forecasts: List<Forecast>) =
        repository.saveCityFullData(city, weather, forecasts)
}

class GetLastSelectedCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(): CityFullData? = repository.getLastSelectedCity()
}

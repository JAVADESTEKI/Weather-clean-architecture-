package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.model.City
import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveSelectedCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: City) = repository.saveSelectedCity(city)
}

class GetLastSelectedCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(): City? = repository.getLastSelectedCity()
}

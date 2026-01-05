package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetLastInsertedIdUseCase  @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke() :Long {
        return repository.getLastInsertedIdUseCase()
    }
}
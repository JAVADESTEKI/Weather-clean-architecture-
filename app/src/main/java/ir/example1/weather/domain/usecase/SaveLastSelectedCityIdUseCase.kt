package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.repository.UserPreferenceRepository
import javax.inject.Inject

class SaveLastSelectedCityIdUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {
    suspend operator fun invoke(cityId: Long) {
        repository.saveLastSelectedCityId(cityId)
    }
}

package ir.example1.weather.domain.usecase

import ir.example1.weather.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastSelectedCityIdUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {
    operator fun invoke(): Flow<Long?> {
        return repository.getLastSelectedCityId()
    }
}

package ir.example1.weather.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    suspend fun saveLastSelectedCityId(cityId: Long)

    fun getLastSelectedCityId(): Flow<Long?>
}

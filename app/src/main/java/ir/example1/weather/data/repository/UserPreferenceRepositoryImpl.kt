package ir.example1.weather.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.example1.weather.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferenceRepository {

    override suspend fun saveLastSelectedCityId(cityId: Long) {
        context.userPreferenceDataStore.edit { preferences ->
            preferences[UserPreferenceKeys.LAST_SELECTED_CITY_ID] = cityId
        }
    }

    override fun getLastSelectedCityId(): Flow<Long?> {
        return context.userPreferenceDataStore.data.map { preferences ->
            preferences[UserPreferenceKeys.LAST_SELECTED_CITY_ID]
        }
    }
}

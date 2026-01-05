package ir.example1.weather.data.preference

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.userPreferenceDataStore by preferencesDataStore(
    name = "user_preferences"
)

object UserPreferenceKeys {
    val LAST_SELECTED_CITY_ID = longPreferencesKey("last_selected_city_id")
}

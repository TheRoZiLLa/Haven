package com.haven.app.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance at the top level
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "home_preferences")

class HomePreferencesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    // Keys for the preferences
    private object PreferencesKeys {
        val LAST_SEED_ID = stringPreferencesKey("last_seed_id")
        val LAST_FOCUS_TIME = intPreferencesKey("last_focus_time")
        val LAST_BREAK_TIME = intPreferencesKey("last_break_time")
    }

    // Flow of the user's home configuration preferences
    val userPreferencesFlow: Flow<HomePreferences> = dataStore.data
        .map { preferences ->
            val seedId = preferences[PreferencesKeys.LAST_SEED_ID] ?: "oak"
            val focusTime = preferences[PreferencesKeys.LAST_FOCUS_TIME] ?: 40
            val breakTime = preferences[PreferencesKeys.LAST_BREAK_TIME] ?: 5
            HomePreferences(seedId, focusTime, breakTime)
        }

    suspend fun updatePreferences(seedId: String, focusTime: Int, breakTime: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SEED_ID] = seedId
            preferences[PreferencesKeys.LAST_FOCUS_TIME] = focusTime
            preferences[PreferencesKeys.LAST_BREAK_TIME] = breakTime
        }
    }
}

data class HomePreferences(
    val lastSeedId: String,
    val lastFocusTime: Int,
    val lastBreakTime: Int
)

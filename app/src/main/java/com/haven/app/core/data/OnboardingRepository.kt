package com.haven.app.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_preferences")

class OnboardingRepository(private val context: Context) {

    private val dataStore = context.onboardingDataStore

    private object PreferencesKeys {
        val HAS_SEEN_INTRO = booleanPreferencesKey("has_seen_intro")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val hasSeenIntroFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_INTRO] ?: false
        }

    val appLanguageFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] ?: "en"
        }

    suspend fun setHasSeenIntro(hasSeen: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_INTRO] = hasSeen
        }
    }

    suspend fun setAppLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = language
        }
    }
}

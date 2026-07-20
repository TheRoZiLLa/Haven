package com.haven.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haven.app.core.data.OnboardingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    val hasSeenIntro: StateFlow<Boolean> = onboardingRepository.hasSeenIntroFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val appLanguage: StateFlow<String> = onboardingRepository.appLanguageFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    fun resetOnboardingIntro() {
        viewModelScope.launch {
            onboardingRepository.setHasSeenIntro(false)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            onboardingRepository.setAppLanguage(language)
        }
    }
}

class SettingsViewModelFactory(
    private val onboardingRepository: OnboardingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(onboardingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

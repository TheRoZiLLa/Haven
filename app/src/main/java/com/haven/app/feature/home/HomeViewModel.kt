package com.haven.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haven.app.core.common.SeedSlot
import com.haven.app.core.common.SeedType
import com.haven.app.core.data.HomePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * HomeViewModel — manages UI state, logic, and DataStore persistence for the Home Screen.
 */
class HomeViewModel(
    application: Application,
    private val repository: HomePreferencesRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning,"
            hour < 17 -> "Good afternoon,"
            else      -> "Good evening,"
        }
        
        viewModelScope.launch {
            // Restore from DataStore
            val prefs = repository.userPreferencesFlow.first()
            val restoredSeed = SeedType.values().find { it.id == prefs.lastSeedId } ?: SeedType.OAK
            
            _uiState.update { 
                it.copy(
                    greeting = greeting,
                    selectedSeed = restoredSeed,
                    focusTimeMinutes = prefs.lastFocusTime,
                    breakTimeMinutes = prefs.lastBreakTime,
                    isLoading = false
                ) 
            }
        }
    }

    private fun saveState() {
        val state = _uiState.value
        state.selectedSeed?.let { seed ->
            viewModelScope.launch {
                repository.updatePreferences(seed.id, state.focusTimeMinutes, state.breakTimeMinutes)
            }
        }
    }

    fun selectSeed(seed: SeedType) {
        _uiState.update { state ->
            val minTime = seed.minFocusMinutes
            val newFocusTime = if (state.focusTimeMinutes < minTime) minTime else state.focusTimeMinutes
            state.copy(selectedSeed = seed, focusTimeMinutes = newFocusTime, errorMessage = null)
        }
        saveState()
    }

    fun increaseFocusTime() {
        _uiState.update { state ->
            val next = state.focusTimeMinutes + 5
            state.copy(focusTimeMinutes = next.coerceAtMost(MAX_FOCUS_MINUTES), errorMessage = null)
        }
        saveState()
    }

    fun decreaseFocusTime() {
        _uiState.update { state ->
            val minTime = state.selectedSeed?.minFocusMinutes ?: MIN_FOCUS_MINUTES
            val prev = state.focusTimeMinutes - 5
            state.copy(focusTimeMinutes = prev.coerceAtLeast(minTime), errorMessage = null)
        }
        saveState()
    }

    fun setFocusTime(minutes: Int) {
        val minTime = _uiState.value.selectedSeed?.minFocusMinutes ?: MIN_FOCUS_MINUTES
        _uiState.update { state ->
            state.copy(focusTimeMinutes = minutes.coerceIn(minTime, MAX_FOCUS_MINUTES), errorMessage = null)
        }
        saveState()
    }

    fun selectBreakTime(minutes: Int) {
        _uiState.update { it.copy(breakTimeMinutes = minutes, errorMessage = null) }
        saveState()
    }

    fun validateAndStart() {
        val state = _uiState.value
        if (state.selectedSeed == null) {
            _uiState.update { it.copy(errorMessage = "Please select a seed first.") }
            return
        }
        if (state.focusTimeMinutes < state.selectedSeed.minFocusMinutes) {
            _uiState.update { it.copy(errorMessage = "Focus time is too short for ${state.selectedSeed.displayName}.") }
            return
        }
        if (state.breakTimeMinutes < 5) {
            _uiState.update { it.copy(errorMessage = "Break time must be at least 5 minutes.") }
            return
        }
        
        // Success
        _uiState.update { it.copy(startSessionEvent = true, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearStartSessionEvent() {
        _uiState.update { it.copy(startSessionEvent = false) }
    }

    companion object {
        const val MIN_FOCUS_MINUTES = 15
        const val MAX_FOCUS_MINUTES = 120
    }
}

class HomeViewModelFactory(
    private val application: Application,
    private val repository: HomePreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class HomeUiState(
    val isLoading: Boolean           = true,
    val greeting: String             = "Welcome back,",
    val userName: String             = "",
    val streakDays: Int              = 0,
    val todayTrees: Int              = 0,
    val todayFocusMinutes: Int       = 0,
    val leafBalance: Int             = 12,
    val selectedSeed: SeedType?      = SeedType.OAK,
    val focusTimeMinutes: Int        = 40,
    val breakTimeMinutes: Int        = 5,
    val seedDetailSheet: SeedType?   = null,
    val errorMessage: String?        = null,
    val startSessionEvent: Boolean   = false,
    val seeds: List<SeedSlot>        = defaultSeeds()
) {
    val subHeadline: String = "Take care of your forest \uD83C\uDF3F"

    val isStartEnabled: Boolean get() = !isLoading

    val canIncreaseFocus: Boolean get() = focusTimeMinutes < HomeViewModel.MAX_FOCUS_MINUTES
    val canDecreaseFocus: Boolean get() {
        val min = selectedSeed?.minFocusMinutes ?: HomeViewModel.MIN_FOCUS_MINUTES
        return focusTimeMinutes > min
    }

    val canIncreaseBreak: Boolean get() = breakTimeMinutes < 30
    val canDecreaseBreak: Boolean get() = breakTimeMinutes > 5

    companion object {
        fun defaultSeeds() = listOf(
            SeedSlot(SeedType.OAK,     isUnlocked = true),
            SeedSlot(SeedType.SAKURA,  isUnlocked = true),
            SeedSlot(SeedType.PINE,    isUnlocked = true),
            SeedSlot(SeedType.MAPLE,   isUnlocked = true),
            SeedSlot(SeedType.WILLOW,  isUnlocked = false)
        )
    }
}

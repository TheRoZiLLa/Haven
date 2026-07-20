package com.haven.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.haven.app.core.data.OnboardingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IntroViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IntroState())
    val state: StateFlow<IntroState> = _state.asStateFlow()

    private var sequenceJob: Job? = null

    fun startStepTimer(stepIndex: Int, readingDelayMs: Long) {
        if (stepIndex != _state.value.currentStepIndex) return

        sequenceJob?.cancel()
        sequenceJob = viewModelScope.launch {
            _state.update { it.copy(
                isTextVisible = true,
                showTapHint = false
            ) }

            // 1. Wait for text fade-in
            delay(IntroAnimation.FADE_IN_DURATION_MS.toLong())

            // 2. Show tap hint
            _state.update { it.copy(showTapHint = true) }

            // 3. Wait for the adaptive reading delay
            delay(readingDelayMs)

            // 4. Start fading out
            _state.update { it.copy(
                isTextVisible = false,
                showTapHint = false
            ) }

            // 5. Wait for fade-out animation
            delay(IntroAnimation.FADE_OUT_DURATION_MS.toLong())

            // 6. Go to next step
            advanceStep()
        }
    }

    private fun advanceStep() {
        val nextIndex = _state.value.currentStepIndex + 1
        if (nextIndex >= introSteps.size) {
            triggerCompletion()
        } else {
            _state.update { it.copy(currentStepIndex = nextIndex) }
        }
    }

    fun handleTap() {
        if (_state.value.isFinished || _state.value.isFadingToBlack) return

        sequenceJob?.cancel()
        sequenceJob = viewModelScope.launch {
            _state.update { it.copy(
                isTextVisible = false,
                showTapHint = false
            ) }
            delay(IntroAnimation.FADE_OUT_DURATION_MS.toLong())
            advanceStep()
        }
    }

    fun skipIntro() {
        triggerCompletion()
    }

    fun pauseSequence() {
        sequenceJob?.cancel()
    }

    private fun triggerCompletion() {
        sequenceJob?.cancel()
        sequenceJob = viewModelScope.launch {
            _state.update { it.copy(
                isTextVisible = false,
                showTapHint = false,
                isFadingToBlack = true
            ) }
            // Wait for fade to black transition
            delay(IntroAnimation.FADE_TO_BLACK_DURATION_MS.toLong())

            // Save hasSeenIntro to DataStore
            onboardingRepository.setHasSeenIntro(true)

            _state.update { it.copy(isFinished = true) }
        }
    }
}

class IntroViewModelFactory(
    private val onboardingRepository: OnboardingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IntroViewModel(onboardingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

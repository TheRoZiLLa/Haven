package com.haven.app.feature.onboarding

data class IntroState(
    val currentStepIndex: Int = 0,
    val isTextVisible: Boolean = false,
    val showTapHint: Boolean = false,
    val isFadingToBlack: Boolean = false,
    val isFinished: Boolean = false
) {
    val currentStep: IntroStep?
        get() = if (currentStepIndex in introSteps.indices) introSteps[currentStepIndex] else null

    val currentSceneIndex: Int
        get() = currentStep?.sceneIndex ?: 0

    val currentTextResId: Int?
        get() = currentStep?.textResId

    val currentIllustrationType: IllustrationType
        get() = currentStep?.scene?.illustrationType ?: IllustrationType.FOREST
}

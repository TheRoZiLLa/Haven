package com.haven.app.feature.onboarding

import com.haven.app.R

enum class IllustrationType(val drawableResId: Int) {
    FOREST(R.drawable.bg_scene1_forest),
    WITHERING(R.drawable.bg_scene2_withering),
    EMPTY_LAND(R.drawable.bg_scene3_empty),
    GLOWING_SEED(R.drawable.bg_scene4_seed)
}

data class IntroScene(
    val id: Int,
    val textResIds: List<Int>,
    val illustrationType: IllustrationType
)

data class IntroStep(
    val sceneIndex: Int,
    val textResId: Int,
    val scene: IntroScene
)

val introScenes = listOf(
    IntroScene(
        id = 1,
        textResIds = listOf(
            R.string.intro_scene1_text1,
            R.string.intro_scene1_text2
        ),
        illustrationType = IllustrationType.FOREST
    ),
    IntroScene(
        id = 2,
        textResIds = listOf(
            R.string.intro_scene2_text1,
            R.string.intro_scene2_text2,
            R.string.intro_scene2_text3
        ),
        illustrationType = IllustrationType.WITHERING
    ),
    IntroScene(
        id = 3,
        textResIds = listOf(
            R.string.intro_scene3_text1,
            R.string.intro_scene3_text2,
            R.string.intro_scene3_text3
        ),
        illustrationType = IllustrationType.EMPTY_LAND
    ),
    IntroScene(
        id = 4,
        textResIds = listOf(
            R.string.intro_scene4_text1,
            R.string.intro_scene4_text2,
            R.string.intro_scene4_text3,
            R.string.intro_scene4_text4,
            R.string.intro_scene4_text5
        ),
        illustrationType = IllustrationType.GLOWING_SEED
    )
)

val introSteps: List<IntroStep> = buildList {
    introScenes.forEachIndexed { sceneIdx, scene ->
        scene.textResIds.forEach { textResId ->
            add(IntroStep(sceneIdx, textResId, scene))
        }
    }
}

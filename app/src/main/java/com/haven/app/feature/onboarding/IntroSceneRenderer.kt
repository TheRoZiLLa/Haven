package com.haven.app.feature.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun IntroSceneRenderer(
    currentSceneIndex: Int,
    modifier: Modifier = Modifier
) {
    // 1. Ken Burns Effect: Infinite scale & translation animations
    val infiniteTransition = rememberInfiniteTransition(label = "KenBurnsEffect")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.03f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = IntroAnimation.KEN_BURNS_SCALE_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KenBurnsScale"
    )

    val translationX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = IntroAnimation.KEN_BURNS_MOVE_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KenBurnsX"
    )

    val translationY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = IntroAnimation.KEN_BURNS_MOVE_DURATION_MS + 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KenBurnsY"
    )

    // 2. Preloading next scene's illustration to avoid rendering stutters
    val nextSceneIndex = currentSceneIndex + 1
    if (nextSceneIndex in introScenes.indices) {
        val nextIllustration = introScenes[nextSceneIndex].illustrationType
        painterResource(id = nextIllustration.drawableResId)
    }

    // 3. Render current scene background with Crossfade and Ken Burns modifiers
    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentSceneIndex,
            animationSpec = tween(durationMillis = IntroAnimation.FADE_IN_DURATION_MS),
            label = "SceneCrossfade"
        ) { sceneIdx ->
            val scene = introScenes[sceneIdx]
            Image(
                painter = painterResource(id = scene.illustrationType.drawableResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = translationX,
                        translationY = translationY
                    )
            )
        }
    }
}

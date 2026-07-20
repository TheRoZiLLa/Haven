package com.haven.app.feature.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haven.app.R
import com.haven.app.core.data.OnboardingRepository

@Composable
fun IntroScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { OnboardingRepository(context.applicationContext) }
    val viewModel: IntroViewModel = viewModel(
        factory = IntroViewModelFactory(repository)
    )
    val uiState by viewModel.state.collectAsState()

    // 1. Observe Lifecycle to pause VM timers when backgrounded
    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    isResumed = true
                }
                Lifecycle.Event.ON_PAUSE -> {
                    isResumed = false
                    viewModel.pauseSequence()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 2. Language-Independent Reading Delay Calculator
    fun calculateReadingDelay(text: String): Long {
        val wordCount = if (text.contains(" ")) {
            text.trim().split("\\s+".toRegex()).size
        } else {
            text.length / 4
        }
        return (IntroAnimation.BASE_READING_DELAY_MS + wordCount * IntroAnimation.WORD_READING_DELAY_MS)
            .coerceIn(IntroAnimation.MIN_READING_DELAY_MS, IntroAnimation.MAX_READING_DELAY_MS)
    }

    // 3. Trigger timer sequences per active step when in the foreground
    LaunchedEffect(uiState.currentStepIndex, isResumed) {
        if (isResumed && !uiState.isFinished && !uiState.isFadingToBlack) {
            uiState.currentTextResId?.let { resId ->
                val text = context.getString(resId)
                val delayMs = calculateReadingDelay(text)
                viewModel.startStepTimer(uiState.currentStepIndex, delayMs)
            }
        }
    }

    // 4. Trigger Navigation on sequence completion
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onFinished()
        }
    }

    // 5. Build Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(id = R.string.intro_tap_to_continue_label)
            ) {
                viewModel.handleTap()
            }
    ) {
        // Background illustration with Ken Burns cinematic zoom
        IntroSceneRenderer(currentSceneIndex = uiState.currentSceneIndex)

        // Bottom vertical gradient overlay for text readability (Transparent -> 45% Black)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                    )
                )
        )

        // Overlay Interactive Content respects Safe Area Insets (Display Cutouts, System Bars)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Skip Button (Top Right)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(id = R.string.intro_skip_content_desc)
                        ) {
                            viewModel.skipIntro()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Skip",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Empty spacer to push content to the bottom half
            Spacer(modifier = Modifier.weight(1f))

            // Narration animations and Bottom container
            val textAlpha by animateFloatAsState(
                targetValue = if (uiState.isTextVisible) 1f else 0f,
                animationSpec = tween(durationMillis = IntroAnimation.FADE_IN_DURATION_MS),
                label = "TextAlphaAnimation"
            )

            val hintAlpha by animateFloatAsState(
                targetValue = if (uiState.showTapHint) 0.45f else 0f,
                animationSpec = tween(durationMillis = 600),
                label = "HintAlphaAnimation"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Narration Text placed in the bottom section
                uiState.currentTextResId?.let { resId ->
                    Text(
                        text = stringResource(id = resId),
                        modifier = Modifier
                            .alpha(textAlpha)
                            .padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light,
                            lineHeight = MaterialTheme.typography.headlineMedium.lineHeight * 1.25,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = androidx.compose.ui.geometry.Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }

                Text(
                    text = stringResource(id = R.string.intro_tap_anywhere_hint),
                    modifier = Modifier.alpha(hintAlpha),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )
                )

                // 4 Scene progress indicators dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isActive = uiState.currentSceneIndex == i
                        val dotSize by animateDpAsState(
                            targetValue = if (isActive) 8.dp else 6.dp,
                            animationSpec = tween(300),
                            label = "DotSize"
                        )
                        val dotAlpha by animateFloatAsState(
                            targetValue = if (isActive) 1f else 0.4f,
                            animationSpec = tween(300),
                            label = "DotAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = dotAlpha))
                        )
                    }
                }
            }
        }

        // Cinematic Fade to Black overlay (300ms) triggered on skip or finish
        val blackOverlayAlpha by animateFloatAsState(
            targetValue = if (uiState.isFadingToBlack) 1f else 0f,
            animationSpec = tween(durationMillis = IntroAnimation.FADE_TO_BLACK_DURATION_MS),
            label = "FadeToBlackAnimation"
        )

        if (blackOverlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = blackOverlayAlpha))
            )
        }
    }
}

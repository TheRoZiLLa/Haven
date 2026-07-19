package com.haven.app.feature.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haven.app.R
import com.haven.app.ui.theme.DarkText
import com.haven.app.ui.theme.MediumGray
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated full-screen popup overlay that appears when the Focus Session is complete.
 * Displays place_holder_animal_popup.png with entry spring scaling and continuous floating sway.
 *
 * Gestures:
 *  - Tap  → [onTap]     (starts the break)
 *  - Swipe left / right / down → [onDismiss] (skips the break)
 *
 * Implementation notes:
 *  - [detectDragGestures] only activates after touch slop is exceeded, so a quick
 *    tap still propagates to the [clickable] modifier and correctly calls [onTap].
 *  - [Modifier.offset] uses a layout-phase lambda so drag position changes do not
 *    trigger full recomposition.
 *  - [graphicsLayer] sway + tilt reads are deferred to the draw phase.
 */
@Composable
fun AnimalOverlayDialog(
    onTap: () -> Unit,
    onDismiss: () -> Unit
) {
    // ─── Entry Animations ──────────────────────────────────────────────────
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val entryScale by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entryScale"
    )
    val entryAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 400),
        label = "entryAlpha"
    )

    // ─── Continuous Floating Sway Animation ──────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "swayAnim")
    val floatSwayYState = infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swayY"
    )

    // ─── Swipe-to-Dismiss State ───────────────────────────────────────────
    val scope = rememberCoroutineScope()
    val offsetXAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(0f) }

    // Dismiss fires when the drag distance in any valid direction exceeds this
    val dismissThreshold = 100.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f)) // Dim background scrim
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Block tap-out: user must interact with the animal */ },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(entryScale)
                .alpha(entryAlpha)
        ) {
            // ─── Floating Animal Container ─────────────────────────────────
            Box(
                modifier = Modifier
                    .size(280.dp)
                    // Layout-phase deferred read: drag position does not cause recomposition
                    .offset {
                        IntOffset(
                            x = offsetXAnim.value.roundToInt(),
                            y = offsetYAnim.value.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        // Sway: draw-phase deferred read reduces unnecessary recompositions
                        translationY = floatSwayYState.value.dp.toPx()

                        // Tilt proportional to horizontal drag (capped at ±18°)
                        val thresholdPx = dismissThreshold.toPx()
                        val dragFraction = (offsetXAnim.value / thresholdPx).coerceIn(-1f, 1f)
                        rotationZ = dragFraction * 18f
                    }
                    // ── Swipe gesture ──────────────────────────────────────
                    // detectDragGestures activates only after touch slop is exceeded,
                    // so a quick tap still reaches the clickable modifier below.
                    .pointerInput(Unit) {
                        val thresholdPx = dismissThreshold.toPx()

                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetXAnim.snapTo(offsetXAnim.value + dragAmount.x)
                                    offsetYAnim.snapTo(offsetYAnim.value + dragAmount.y)
                                }
                            },
                            onDragEnd = {
                                val x = offsetXAnim.value
                                val y = offsetYAnim.value

                                val dismissHorizontal = abs(x) > thresholdPx
                                val dismissDown     = y > thresholdPx

                                if (dismissHorizontal || dismissDown) {
                                    // Fly off screen in the swipe direction
                                    val targetX = when {
                                        dismissHorizontal -> if (x > 0f) 2000f else -2000f
                                        else              -> x * 0.5f
                                    }
                                    val targetY = if (dismissDown) 2000f else y * 0.5f

                                    scope.launch {
                                        launch { offsetXAnim.animateTo(targetX, tween(durationMillis = 250)) }
                                        launch { offsetYAnim.animateTo(targetY, tween(durationMillis = 250)) }
                                    }
                                    scope.launch {
                                        delay(220L)
                                        onDismiss()
                                    }
                                } else {
                                    // Threshold not met — spring back to rest
                                    scope.launch {
                                        launch {
                                            offsetXAnim.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                        launch {
                                            offsetYAnim.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onDragCancel = {
                                // System interrupted the gesture — spring back cleanly
                                scope.launch {
                                    launch { offsetXAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                                    launch { offsetYAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                                }
                            }
                        )
                    }
                    // ── Tap gesture ────────────────────────────────────────
                    // Fires only when no drag slop has been exceeded (i.e. a genuine tap)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTap() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.place_holder_animal_popup),
                    contentDescription = "Forest Friend Waiting",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Subtitle / Hint Card ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🦌 Forest friend waiting!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to start · Swipe ←↓→ to skip",
                        fontSize = 13.sp,
                        color = MediumGray
                    )
                }
            }
        }
    }
}

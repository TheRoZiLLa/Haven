package com.haven.app.feature.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haven.app.R
import com.haven.app.ui.theme.DarkText
import com.haven.app.ui.theme.MediumGray

/**
 * Animated full-screen popup overlay that appears when the Focus Session is complete.
 * Displays place_holder_animal_popup.png with entry spring scaling and continuous floating sway.
 */
@Composable
fun AnimalOverlayDialog(
    onTap: () -> Unit
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.25f)) // Dim background scrim
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* block tap-out for game-loop force tap animal */ },
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
            // Floating Animal Popup Container
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        // Reading floatSwayYState.value here (draw phase) instead of
                        // in the composable body reduces unnecessary recompositions
                        // caused by the infinite sway animation.
                        translationY = floatSwayYState.value.dp.toPx()
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onTap()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.place_holder_animal_popup),
                    contentDescription = "Forest Friend Waiting",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subtitle Guidance Card
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
                        text = "Tap the animal to start your break",
                        fontSize = 13.sp,
                        color = MediumGray
                    )
                }
            }
        }
    }
}

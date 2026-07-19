package com.haven.app.feature.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haven.app.R
import com.haven.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Configuration for floating eco particles.
 */
data class ParticleConfig(
    val xOffset: Float,
    val startY: Float,
    val endY: Float,
    val size: Float
)

/**
 * High-Fidelity UI based on draft1.png mockup (Non-scrollable version).
 * Incorporates centered logo_text asset, sapling circle with bouncy/glow/particle animations,
 * progress circle track, and side-by-side white capsule time selectors.
 */
@Composable
fun RedesignCanvasScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    // ─── State Management ──────────────────────────────────────────────────
    var focusTime by remember { mutableStateOf(25) }
    var breakTime by remember { mutableStateOf(5) }
    var previousBreakTime by remember { mutableStateOf(breakTime) }

    // ─── Animation States ──────────────────────────────────────────────────
    val coroutineScope = rememberCoroutineScope()
    
    // Sapling scale jump (spring)
    val saplingScaleAnim = remember { Animatable(1.0f) }
    
    // Glowing pulse ring
    val glowAlphaAnim = remember { Animatable(0.0f) }
    val glowScaleAnim = remember { Animatable(1.0f) }
    
    // Floating leaf particles
    val particleProgress = remember { Animatable(0.0f) }

    // Eco particles layout parameters
    val particles = remember {
        listOf(
            ParticleConfig(xOffset = -50f, startY = 80f, endY = -60f, size = 18f),
            ParticleConfig(xOffset = -20f, startY = 50f, endY = -90f, size = 12f),
            ParticleConfig(xOffset = 10f, startY = 70f, endY = -70f, size = 22f),
            ParticleConfig(xOffset = 35f, startY = 90f, endY = -50f, size = 14f),
            ParticleConfig(xOffset = 0f, startY = 40f, endY = -110f, size = 16f)
        )
    }

    // Trigger animations when breakTime is increased
    LaunchedEffect(breakTime) {
        if (breakTime > previousBreakTime) {
            // Trigger sapling bouncy jump
            coroutineScope.launch {
                saplingScaleAnim.animateTo(
                    targetValue = 1.25f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                saplingScaleAnim.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }

            // Trigger glowing pulse ring
            coroutineScope.launch {
                glowAlphaAnim.snapTo(0.8f)
                glowScaleAnim.snapTo(1.0f)
                launch {
                    glowAlphaAnim.animateTo(
                        targetValue = 0.0f,
                        animationSpec = tween(durationMillis = 800, easing = EaseOutQuad)
                    )
                }
                launch {
                    glowScaleAnim.animateTo(
                        targetValue = 1.35f,
                        animationSpec = tween(durationMillis = 800, easing = EaseOutQuad)
                    )
                }
            }

            // Trigger floating leaf particles
            coroutineScope.launch {
                particleProgress.snapTo(0.0f)
                particleProgress.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
                )
            }
        }
        previousBreakTime = breakTime
    }

    // ─── Button Press Micro-interaction ────────────────────────────────────
    var isStartPressed by remember { mutableStateOf(false) }
    val startButtonScale by animateFloatAsState(
        targetValue = if (isStartPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "startButtonScale"
    )

    // ─── Root Container ────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9FBF7), // Ivory top
                        Color(0xFFEBF3E6)  // Soft warm mint bottom
                    )
                )
            )
    ) {
        // 1. Bottom Illustration Decoration (bg_tree)
        Image(
            painter = painterResource(id = R.drawable.bg_tree),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        // 2. Main Content Layout (Non-scrollable)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ─── TOP BAR SECTION ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                // Menu Hamburger Button on Left
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterStart)
                        .clickable { onNavigateToSettings() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Menu,
                            contentDescription = "Menu",
                            tint = ForestGreenDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Centered Logo Asset & Subtitle (Larger Size)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_text),
                        contentDescription = "HAVEN",
                        modifier = Modifier.height(34.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Grow with every break",
                        fontSize = 11.sp,
                        color = MediumGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ─── CENTER CIRCLE (SAPLING / TREE GROWTH VIEW) ───
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glowing Pulse Ring
                if (glowAlphaAnim.value > 0f) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .scale(glowScaleAnim.value)
                            .border(
                                width = 4.dp,
                                color = LeafGreen.copy(alpha = glowAlphaAnim.value),
                                shape = CircleShape
                            )
                    )
                }

                // Dynamic Circular Progress Arc (outside the white container)
                val breakProgress = (breakTime / 10f).coerceIn(0f, 1f)
                Canvas(
                    modifier = Modifier.size(272.dp)
                ) {
                    // Draw base faint track
                    drawArc(
                        color = Color(0xFFECEFEA),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                    // Draw active progress arc
                    if (breakProgress > 0f) {
                        drawArc(
                            color = LeafGreen,
                            startAngle = -90f,
                            sweepAngle = breakProgress * 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 6.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                // Circular White Container
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .scale(saplingScaleAnim.value)
                        .shadow(16.dp, CircleShape, spotColor = WarmShadow)
                        .border(1.dp, Color(0xFFE8EFE5), CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    val treeDrawable = if (breakTime >= 10) R.drawable.fully_grown_tree else R.drawable.sapling
                    Image(
                        painter = painterResource(id = treeDrawable),
                        contentDescription = "Tree Status",
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.Center)
                    )
                }

                // Floating Eco Particles (Leaf particles drifting upward)
                if (particleProgress.value > 0.0f && particleProgress.value < 1.0f) {
                    val progress = particleProgress.value
                    val alpha = if (progress < 0.2f) progress / 0.2f else (1.0f - progress) / 0.8f
                    
                    for (i in particles.indices) {
                        val particle = particles[i]
                        val currentY = (particle.startY + (particle.endY - particle.startY) * progress).dp
                        val sway = (kotlin.math.sin(progress * Math.PI * 2 + i).toFloat() * 16f).dp
                        val xOffset = (particle.xOffset).dp
                        val size = (particle.size).dp
                        
                        Icon(
                            imageVector = Icons.Rounded.Eco,
                            contentDescription = null,
                            tint = LeafGreen.copy(alpha = alpha),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = xOffset + sway, y = currentY)
                                .size(size)
                                .graphicsLayer {
                                    rotationZ = progress * 240f + (i * 30)
                                    scaleX = 1.0f - (progress * 0.2f)
                                    scaleY = 1.0f - (progress * 0.2f)
                                }
                        )
                    }
                }
            }

            // ─── TIME CONFIGURATION CAPSULES ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Focus Time Capsule
                TimeSelectorCapsule(
                    modifier = Modifier.weight(1f),
                    label = "Focus Time",
                    value = focusTime,
                    onDecrease = { focusTime = (focusTime - 1).coerceAtLeast(15) },
                    onIncrease = { focusTime = (focusTime + 1).coerceAtMost(120) }
                )

                // Break Time Capsule
                TimeSelectorCapsule(
                    modifier = Modifier.weight(1f),
                    label = "Break Time",
                    value = breakTime,
                    onDecrease = { breakTime = (breakTime - 1).coerceAtLeast(5) },
                    onIncrease = { breakTime = (breakTime + 1).coerceAtMost(30) }
                )
            }

            // ─── START CTA BUTTON ───
            Button(
                onClick = { /* Handle timer start */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(startButtonScale)
                    .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ForestGreen),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81C784),
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = "Start Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Customizable inline time adjustment capsule.
 */
@Composable
fun TimeSelectorCapsule(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(40.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(40.dp), spotColor = WarmShadow)
            .border(1.dp, Color(0xFFE8EFE5), RoundedCornerShape(40.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp)
        ) {
            // Label Header
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Adjuster Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Minus Text Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDecrease() }
                ) {
                    Text(
                        text = "–",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }

                // Centered Value digit
                AnimatedContent(
                    targetState = value,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { height -> height } + fadeIn() togetherWith
                             slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height } + fadeIn() togetherWith
                             slideOutVertically { height -> height } + fadeOut())
                        }
                    },
                    label = "timeAnim",
                    modifier = Modifier.width(56.dp),
                    contentAlignment = Alignment.Center
                ) { targetTime ->
                    Text(
                        text = targetTime.toString(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Plus Text Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onIncrease() }
                ) {
                    Text(
                        text = "+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Unit Footer
            Text(
                text = "min",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText.copy(alpha = 0.8f)
            )
        }
    }
}

package com.haven.app.feature.timer

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haven.app.ui.theme.*

/**
 * Screen showing the live countdown of the Focus Session.
 * Synchronizes with FocusService background state using lifecycle-aware collection.
 */
@Composable
fun FocusTimerScreen(
    seedId: String,
    focusMinutes: Int,
    breakMinutes: Int,
    isDebugSession: Boolean,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    
    // Request notification permission on Android 13+
    val postNotificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Start the service
        FocusService.start(context, seedId, focusMinutes, breakMinutes, isDebugSession)
    }

    LaunchedEffect(Unit) {
        // 1. Check/request overlay permission
        if (!canOverlaysDraw(context)) {
            val intent = Intent(
                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }

        // 2. Request notification permission & start service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission) {
                postNotificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                FocusService.start(context, seedId, focusMinutes, breakMinutes, isDebugSession)
            }
        } else {
            FocusService.start(context, seedId, focusMinutes, breakMinutes, isDebugSession)
        }
    }

    // Keep screen awake during session
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Extract live ticking states from the service using lifecycle-aware collection
    val isPaused by FocusService.isPausedFlow.collectAsStateWithLifecycle()
    val remainingSeconds by FocusService.remainingSecondsFlow.collectAsStateWithLifecycle()
    val totalSecondsState by FocusService.totalSecondsFlow.collectAsStateWithLifecycle()
    val totalSeconds = if (totalSecondsState > 0) totalSecondsState else (focusMinutes * 60)

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9FBF7),
                        Color(0xFFEBF3E6)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 48.dp, horizontal = 24.dp)
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🌱 Focus Session",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
                Text(
                    text = "Growing: ${seedId.replaceFirstChar { it.uppercase() }}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGray
                )
            }

            // Circular Countdown Timer
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                // Faint Background Track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFFECEFEA),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Active Progress Arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = LeafGreen,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Centered Digits (using unified format formatter)
                Text(
                    text = FocusService.formatRemainingTime(remainingSeconds),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreenDark
                )
            }

            // Controls Row (Pause/Resume, Cancel)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Session Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(64.dp)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                        .clickable {
                            FocusService.cancel(context)
                            onCancel()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cancel focus",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Play / Pause Button
                Surface(
                    shape = CircleShape,
                    color = LeafGreen,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable {
                            if (isPaused) {
                                FocusService.resume(context)
                            } else {
                                FocusService.pause(context)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                            contentDescription = if (isPaused) "Resume focus" else "Pause focus",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Extension helper to check overlay draws
 */
private fun canOverlaysDraw(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        android.provider.Settings.canDrawOverlays(context)
    } else {
        true
    }
}

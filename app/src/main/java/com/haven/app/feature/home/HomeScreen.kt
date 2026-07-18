package com.haven.app.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haven.app.core.common.SeedSlot
import com.haven.app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToForest: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onStartSession: (SeedSlot, Int, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.startSessionEvent) {
        if (uiState.startSessionEvent) {
            uiState.selectedSeed?.let { seed ->
                onStartSession(SeedSlot(seed), uiState.focusTimeMinutes, uiState.breakTimeMinutes)
            }
            viewModel.clearStartSessionEvent()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9)) // Base background fallback
    ) {
        // 1. Background Layer (Forest 3D Placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f) // Reduced from 0.45f to give more room to the UI
                .background(Mint.copy(alpha = 0.5f))
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HAVEN Logo Placeholder
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(24.dp).offset(y = (-6).dp, x = 12.dp)
                    )
                    Text(
                        text = "HAVEN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreenDark,
                        letterSpacing = 1.sp
                    )
                }

                // Top Right Icons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Mint.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.BarChart, contentDescription = "Stats", tint = ForestGreenDark, modifier = Modifier.size(20.dp))
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = WarmWhite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.background(SoftYellow.copy(alpha = 0.3f))) {
                            Text("🦌", fontSize = 20.sp)
                        }
                    }
                }
            }

            // Welcome Text
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, top = 100.dp) // Adjusted top padding
            ) {
                Text(
                    text = uiState.greeting,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ForestGreenDark.copy(alpha = 0.8f)
                )
                Text(
                    text = uiState.subHeadline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenDark
                )
            }
        }

        // 2. Foreground White Sheet (No Scrolling)
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = WarmWhite,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f) // Takes the bottom 68% of the screen
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Removed verticalScroll to ensure it fits entirely on one screen
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween // Distributes space evenly without squishing
            ) {
                // --- SECTION: TIME CONFIG ---
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Eco, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Set your focus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimePickerCard(
                            modifier = Modifier.weight(1f),
                            title = "Play Time",
                            icon = Icons.Rounded.Gamepad,
                            value = uiState.focusTimeMinutes,
                            onUp = { viewModel.increaseFocusTime() },
                            onDown = { viewModel.decreaseFocusTime() }
                        )
                        TimePickerCard(
                            modifier = Modifier.weight(1f),
                            title = "Break Time",
                            icon = Icons.Rounded.Coffee,
                            value = uiState.breakTimeMinutes,
                            onUp = { viewModel.selectBreakTime((uiState.breakTimeMinutes + 5).coerceAtMost(30)) },
                            onDown = { viewModel.selectBreakTime((uiState.breakTimeMinutes - 5).coerceAtLeast(5)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(40 to 5, 50 to 10, 60 to 10, 90 to 15)
                        presets.forEach { (focus, breakT) ->
                            val isSelected = uiState.focusTimeMinutes == focus && uiState.breakTimeMinutes == breakT
                            QuickSelectPill(
                                label = "$focus / $breakT",
                                isSelected = isSelected,
                                onClick = {
                                    viewModel.setFocusTime(focus)
                                    viewModel.selectBreakTime(breakT)
                                }
                            )
                        }
                        QuickSelectPill(label = "⎈ Custom", isSelected = false, onClick = {})
                    }
                }

                // --- SECTION: SEED SELECTION ---
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Eco, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Choose your seed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Filled.Info, contentDescription = "Info", tint = MediumGray, modifier = Modifier.size(14.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = Mint.copy(alpha = 0.5f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Eco, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = uiState.leafBalance.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.seeds.forEach { slot ->
                            SeedCardUI(
                                slot = slot,
                                isSelected = uiState.selectedSeed == slot.seedType,
                                onClick = { if (slot.isUnlocked) viewModel.selectSeed(slot.seedType) }
                            )
                        }
                    }
                }

                // --- SECTION: START BUTTON ---
                // Info Banner removed to keep UI simple, clean, and fit on one screen without scrolling.
                
                Button(
                    onClick = { viewModel.validateAndStart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LeafGreen,
                        contentColor = WarmWhite,
                        disabledContainerColor = LightGray
                    ),
                    enabled = uiState.isStartEnabled
                ) {
                    Icon(Icons.Rounded.Eco, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Focus", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun TimePickerCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    onUp: () -> Unit,
    onDown: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = WarmWhite,
        border = BorderStroke(1.dp, Mint),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(title, fontSize = 12.sp, color = ForestGreenDark, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(icon, contentDescription = null, tint = LeafGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "min",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = CircleShape,
                    color = Mint.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp).clickable { onUp() }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Up", tint = ForestGreenDark, modifier = Modifier.padding(4.dp))
                }
                Surface(
                    shape = CircleShape,
                    color = Mint.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp).clickable { onDown() }
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Down", tint = ForestGreenDark, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun QuickSelectPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) LeafGreen else Color.Transparent,
        border = if (!isSelected) BorderStroke(1.dp, Mint) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected && label != "⎈ Custom") {
                Icon(Icons.Rounded.Eco, contentDescription = null, tint = WarmWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) WarmWhite else ForestGreenDark
            )
        }
    }
}

@Composable
fun SeedCardUI(
    slot: SeedSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Mint.copy(alpha = 0.3f) else WarmWhite,
        border = BorderStroke(1.dp, if (isSelected) LeafGreen else Mint.copy(alpha = 0.5f)),
        modifier = Modifier
            .width(88.dp)
            .height(120.dp)
            .clickable(enabled = slot.isUnlocked) { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!slot.isUnlocked) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = MediumGray, modifier = Modifier.size(16.dp).align(Alignment.End))
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (slot.isUnlocked) Color.Transparent else LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slot.seedType.emoji,
                    fontSize = 32.sp,
                    color = if (slot.isUnlocked) Color.Unspecified else MediumGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = slot.seedType.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (slot.isUnlocked) ForestGreenDark else MediumGray
            )
            Text(
                text = slot.seedType.tag,
                fontSize = 11.sp,
                color = MediumGray
            )
        }
    }
}

package com.haven.app.feature.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.haven.app.core.common.SeedType
import com.haven.app.ui.theme.DarkText
import com.haven.app.ui.theme.ForestGreen
import com.haven.app.ui.theme.ForestGreenDark
import com.haven.app.ui.theme.LeafGreen
import com.haven.app.ui.theme.LightGray
import com.haven.app.ui.theme.MediumGray
import com.haven.app.ui.theme.Mint
import com.haven.app.ui.theme.ShapeBottomSheet
import com.haven.app.ui.theme.ShapeButton
import com.haven.app.ui.theme.WarmWhite

/**
 * Seed Detail Bottom Sheet — shown on long-press of a Seed Card.
 * Displays seed description, minimum focus time, and a "Select This Seed" button.
 */
@Composable
fun SeedDetailBottomSheet(
    seed: SeedType,
    onDismiss: () -> Unit,
    onSelect: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier  = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = {}  // consume click — don't dismiss
                    ),
                shape     = ShapeBottomSheet,
                color     = WarmWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(LightGray)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Seed emoji (large)
                    Text(text = seed.emoji, fontSize = 72.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name + rarity
                    Text(
                        text  = seed.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = CircleShape,
                        color = when (seed.tag) {
                            "Rare", "Creativity" -> Mint.copy(alpha = 0.8f)
                            "Uncommon", "Calm" -> LeafGreen.copy(alpha = 0.3f)
                            else -> LightGray
                        }
                    ) {
                        Text(
                            text     = seed.tag,
                            style    = MaterialTheme.typography.labelMedium,
                            color    = ForestGreenDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text      = seed.description,
                        style     = MaterialTheme.typography.bodyLarge,
                        color     = MediumGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Info row
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .background(LightGray, MaterialTheme.shapes.large)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text  = "Minimum Focus",
                                style = MaterialTheme.typography.labelMedium,
                                color = MediumGray
                            )
                            Text(
                                text  = "${seed.minFocusMinutes} minutes",
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkText
                            )
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(
                                text  = "Reward",
                                style = MaterialTheme.typography.labelMedium,
                                color = MediumGray
                            )
                            Text(
                                text  = "+1 ${seed.displayName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = ForestGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Select button
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue   = if (isPressed) 0.96f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessMedium
                        ),
                        label = "select_btn_scale"
                    )

                    Button(
                        onClick           = onSelect,
                        modifier          = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .scale(scale),
                        shape             = ShapeButton,
                        colors            = ButtonDefaults.buttonColors(
                            containerColor = ForestGreen,
                            contentColor   = WarmWhite
                        ),
                        elevation         = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        interactionSource = interactionSource
                    ) {
                        Text(
                            text  = "Select This Seed",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

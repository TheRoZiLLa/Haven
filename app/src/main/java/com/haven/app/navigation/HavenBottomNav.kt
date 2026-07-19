package com.haven.app.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.haven.app.ui.theme.ForestGreen
import com.haven.app.ui.theme.MediumGray
import com.haven.app.ui.theme.Mint
import com.haven.app.ui.theme.WarmShadow

private data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    NavItem(Routes.HOME, "Timer", Icons.Filled.Timer, Icons.Outlined.Timer),
    NavItem(Routes.FOREST, "Forest", Icons.Filled.Park, Icons.Outlined.Park),
    NavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

/**
 * A floating liquid-glass bar made from exactly two surface layers: the glass
 * shell and one selection pill that glides beneath the active destination.
 */
@Composable
fun HavenBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(32.dp)
    val selectedIndex = bottomNavItems.indexOfFirst { it.route == currentRoute }
        .coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 10.dp)
            .height(64.dp)
            .widthIn(max = 390.dp)
            .shadow(16.dp, barShape, ambientColor = WarmShadow, spotColor = WarmShadow)
            .clip(barShape)
            // Background layer 1: a clean, opaque white shell.
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            val itemWidth = maxWidth / bottomNavItems.size
            val pillOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = tween(420, easing = FastOutSlowInEasing),
                label = "liquid_pill_position"
            )

            // Background layer 2: the only active-state surface.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = pillOffset)
                    .width(itemWidth)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Mint)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    LiquidGlassNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidGlassNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "nav_icon_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.58f,
        animationSpec = tween(220),
        label = "nav_content_alpha"
    )
    val contentColor = if (isSelected) ForestGreen else MediumGray

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = contentColor.copy(alpha = contentAlpha),
            modifier = Modifier
                .size(22.dp)
                .scale(iconScale)
        )
        Spacer(Modifier.height(1.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor.copy(alpha = contentAlpha)
        )
    }
}

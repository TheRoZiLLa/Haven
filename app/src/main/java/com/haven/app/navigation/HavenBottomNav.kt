package com.haven.app.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.haven.app.ui.theme.ForestGreen
import com.haven.app.ui.theme.MediumGray
import com.haven.app.ui.theme.Mint
import com.haven.app.ui.theme.ShapeBottomNav
import com.haven.app.ui.theme.WarmWhite

data class NavItem(
    val route:        String,
    val label:        String,
    val iconActive:   ImageVector,
    val iconInactive: ImageVector
)

val bottomNavItems = listOf(
    NavItem(Routes.HOME,      "Home",     Icons.Filled.Home,       Icons.Outlined.Home),
    NavItem(Routes.FOREST,    "Forest",   Icons.Filled.Park,       Icons.Outlined.Park),
    NavItem(Routes.MISSIONS,  "Missions", Icons.Filled.Star,       Icons.Outlined.StarBorder),
    NavItem(Routes.SHOP,      "Shop",     Icons.Filled.Storefront, Icons.Outlined.Storefront),
    NavItem(Routes.PROFILE,   "Profile",  Icons.Filled.Person,     Icons.Outlined.Person),
)

/**
 * HAVEN Floating Bottom Navigation Bar.
 * Pill-shaped indicator slides between tabs on selection.
 * Inset from screen edges with warm shadow, frosted-glass feel.
 */
@Composable
fun HavenBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape           = ShapeBottomNav,
        color           = WarmWhite,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                NavBarItem(
                    item        = item,
                    isSelected  = currentRoute == item.route,
                    onClick     = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "nav_item_scale"
    )

    Column(
        modifier            = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pill indicator background
        Box(contentAlignment = Alignment.Center) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 32.dp)
                        .background(Mint, androidx.compose.foundation.shape.RoundedCornerShape(50))
                )
            }
            Icon(
                imageVector        = if (isSelected) item.iconActive else item.iconInactive,
                contentDescription = item.label,
                tint               = if (isSelected) ForestGreen else MediumGray,
                modifier           = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text  = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) ForestGreen else MediumGray
        )
    }
}

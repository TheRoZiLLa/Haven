package com.haven.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * HAVEN Shapes — No sharp edges. Minimum radius 12dp everywhere.
 *
 * sm  = 12dp  — chips, badges
 * md  = 16dp  — buttons, input fields
 * lg  = 24dp  — standard cards
 * xl  = 32dp  — large cards, bottom sheets
 */
val HavenShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),  // chips, badges
    small      = RoundedCornerShape(12.dp),  // chips
    medium     = RoundedCornerShape(16.dp),  // buttons, inputs
    large      = RoundedCornerShape(24.dp),  // standard cards
    extraLarge = RoundedCornerShape(32.dp),  // bottom sheets, forest card
)

// Additional shape constants used directly in composables
val ShapeCard        = RoundedCornerShape(24.dp)
val ShapeCardLarge   = RoundedCornerShape(32.dp)
val ShapeButton      = RoundedCornerShape(16.dp)
val ShapeChip        = RoundedCornerShape(50)   // full pill
val ShapeBadge       = RoundedCornerShape(12.dp)
val ShapeSeedCard    = RoundedCornerShape(24.dp)
val ShapeBottomSheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val ShapeBottomNav   = RoundedCornerShape(24.dp)

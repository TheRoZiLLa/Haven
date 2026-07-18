package com.haven.app.core.common

/**
 * Defines the types of seeds available in HAVEN.
 * Corresponds to the visual reference image.
 */
enum class SeedType(
    val id: String,
    val displayName: String,
    val tag: String,
    val emoji: String,
    val minFocusMinutes: Int,
    val description: String
) {
    OAK(
        id = "oak",
        displayName = "Oak",
        tag = "Balanced",
        emoji = "🌰", // Acorn
        minFocusMinutes = 15,
        description = "A strong and balanced foundation."
    ),
    SAKURA(
        id = "sakura",
        displayName = "Sakura",
        tag = "Calm",
        emoji = "🌸",
        minFocusMinutes = 20,
        description = "Provides a calming focus session."
    ),
    PINE(
        id = "pine",
        displayName = "Pine",
        tag = "Focus",
        emoji = "🌲",
        minFocusMinutes = 25,
        description = "Sharp and persistent focus."
    ),
    MAPLE(
        id = "maple",
        displayName = "Maple",
        tag = "Creativity",
        emoji = "🍁",
        minFocusMinutes = 30,
        description = "Inspires creative thinking."
    ),
    WILLOW(
        id = "willow",
        displayName = "Willow",
        tag = "Locked",
        emoji = "💧",
        minFocusMinutes = 45,
        description = "A deep and immersive focus."
    );

    companion object {
        val default = OAK
    }
}

/**
 * Represents a slot in the seed selection UI, combining the static SeedType
 * with dynamic user progression data (unlocked state).
 */
data class SeedSlot(
    val seedType: SeedType,
    val isUnlocked: Boolean = false,
    val unlockLevel: Int = 1
)

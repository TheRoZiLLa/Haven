package com.haven.app.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global navigation event routing for prototype intent redirect.
 */
object NavigationTarget {
    var pendingRoute by mutableStateOf<String?>(null)
}

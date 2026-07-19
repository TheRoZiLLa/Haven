package com.haven.app.navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.haven.app.feature.home.RedesignCanvasScreen
import com.haven.app.feature.timer.FocusTimerScreen
import com.haven.app.feature.timer.FocusService
import com.haven.app.feature.timer.AnimalOverlayDialog
import com.haven.app.feature.timer.BreakSelectionScreen
import com.haven.app.feature.timer.BreakCountdownScreen
import com.haven.app.feature.timer.BreakCompleteScreen
import com.haven.app.ui.theme.WarmWhite

/**
 * App-level navigation routes.
 */
object Routes {
    const val HOME            = "home"
    const val FOREST          = "forest"
    const val MISSIONS        = "missions"
    const val SHOP            = "shop"
    const val PROFILE         = "profile"
    const val TIMER           = "timer"
    const val SETTINGS        = "settings"
    const val ANIMAL_OVERLAY  = "animal_overlay"
    const val BREAK_SELECTION = "break_selection"
    const val BREAK_COUNTDOWN = "break_countdown"
    const val BREAK_COMPLETE  = "break_complete"
}

/**
 * Root navigation host with scaffold + floating bottom navigation.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    // Observe global NavigationTarget to route one-shot intents
    LaunchedEffect(NavigationTarget.pendingRoute) {
        NavigationTarget.pendingRoute?.let { route ->
            navController.navigate(route) {
                if (route == Routes.ANIMAL_OVERLAY) {
                    popUpTo(Routes.HOME) { inclusive = false }
                }
            }
            NavigationTarget.pendingRoute = null
        }
    }

    // Screens that show the bottom nav
    val showBottomNav = currentRoute in listOf(
        Routes.FOREST, Routes.MISSIONS, Routes.SHOP, Routes.PROFILE
    )

    Scaffold(
        containerColor = WarmWhite,
        bottomBar = {
            if (showBottomNav) {
                HavenBottomNav(
                    currentRoute = currentRoute,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Routes.HOME,
            modifier         = if (showBottomNav) Modifier.padding(innerPadding) else Modifier
        ) {
            composable(Routes.HOME) {
                RedesignCanvasScreen(
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onStartTimer = { seedId, focusTime, breakTime, isDebug ->
                        navController.navigate("${Routes.TIMER}/$seedId/$focusTime/$breakTime/$isDebug")
                    }
                )
            }

            composable(Routes.SETTINGS) {
                PlaceholderScreen(label = "⚙️ Settings\n\nComing soon")
            }

            composable(Routes.FOREST) {
                PlaceholderScreen(label = "🌲 Forest\n\nComing soon")
            }

            composable(Routes.MISSIONS) {
                PlaceholderScreen(label = "🏵️ Missions\n\nComing soon")
            }

            composable(Routes.SHOP) {
                PlaceholderScreen(label = "🏪 Shop\n\nComing soon")
            }

            composable(Routes.PROFILE) {
                PlaceholderScreen(label = "👤 Profile\n\nComing soon")
            }

            composable(
                route = "${Routes.TIMER}/{seedId}/{focusTime}/{breakTime}/{isDebug}",
                arguments = listOf(
                    navArgument("seedId") { type = NavType.StringType },
                    navArgument("focusTime") { type = NavType.IntType },
                    navArgument("breakTime") { type = NavType.IntType },
                    navArgument("isDebug") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val seedId = backStackEntry.arguments?.getString("seedId") ?: "oak"
                val focusTime = backStackEntry.arguments?.getInt("focusTime") ?: 25
                val breakTime = backStackEntry.arguments?.getInt("breakTime") ?: 5
                val isDebug = backStackEntry.arguments?.getBoolean("isDebug") ?: false
                
                FocusTimerScreen(
                    seedId = seedId,
                    focusMinutes = focusTime,
                    breakMinutes = breakTime,
                    isDebugSession = isDebug,
                    onCancel = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                )
            }

            composable(Routes.ANIMAL_OVERLAY) {
                AnimalOverlayDialog(
                    onTap = {
                        val breakTime = FocusService.breakTimeMinutes
                        navController.navigate("${Routes.BREAK_SELECTION}/$breakTime") {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = "${Routes.BREAK_SELECTION}/{breakTime}",
                arguments = listOf(navArgument("breakTime") { type = NavType.IntType })
            ) { backStackEntry ->
                val breakTime = backStackEntry.arguments?.getInt("breakTime") ?: 5
                BreakSelectionScreen(
                    onSelectNormalBreak = {
                        navController.navigate("${Routes.BREAK_COUNTDOWN}/$breakTime")
                    },
                    onCancel = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                )
            }

            composable(
                route = "${Routes.BREAK_COUNTDOWN}/{breakTime}",
                arguments = listOf(navArgument("breakTime") { type = NavType.IntType })
            ) { backStackEntry ->
                val breakTime = backStackEntry.arguments?.getInt("breakTime") ?: 5
                BreakCountdownScreen(
                    breakMinutes = breakTime,
                    onCancel = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    },
                    onComplete = {
                        navController.navigate(Routes.BREAK_COMPLETE) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                )
            }

            composable(Routes.BREAK_COMPLETE) {
                BreakCompleteScreen(
                    onReturnHome = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                )
            }
        }
    }
}

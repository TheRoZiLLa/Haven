package com.haven.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

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
 * Root navigation host with a bottom navigation overlay. The overlay lets the
 * screen artwork remain visible through the floating glass surface.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val predictiveNavReveal = remember { Animatable(0f) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    // Observe global NavigationTarget to route one-shot intents
    LaunchedEffect(NavigationTarget.pendingRoute) {
        NavigationTarget.pendingRoute?.let { route ->
            navController.navigate(route) {
                if (route == Routes.ANIMAL_OVERLAY || route.startsWith(Routes.BREAK_SELECTION)) {
                    popUpTo(Routes.HOME) { inclusive = false }
                }
            }
            NavigationTarget.pendingRoute = null
        }
    }

    // Screens that show the bottom nav
    val showBottomNav = currentRoute in listOf(Routes.HOME, Routes.FOREST, Routes.SETTINGS)
    val isFocusTimer = currentRoute.startsWith("${Routes.TIMER}/")

    // While an Android predictive-back gesture is in progress from the focus
    // timer, reveal the Timer tab in sync with the user's swipe.
    PredictiveBackHandler(enabled = isFocusTimer) { progressEvents ->
        try {
            progressEvents.collect { event ->
                predictiveNavReveal.snapTo(event.progress)
            }
            navController.popBackStack()
        } finally {
            withContext(NonCancellable) {
                predictiveNavReveal.snapTo(0f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController    = navController,
            startDestination = Routes.HOME,
            modifier         = Modifier.fillMaxSize()
        ) {
            composable(
                route = Routes.HOME,
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(350)) { -it / 10 } },
                exitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(250)) { -it / 12 } }
            ) {
                RedesignCanvasScreen(
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onStartTimer = { seedId, focusTime, breakTime, isDebug ->
                        navController.navigate("${Routes.TIMER}/$seedId/$focusTime/$breakTime/$isDebug")
                    }
                )
            }

            composable(
                route = Routes.SETTINGS,
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(350)) { it / 10 } },
                exitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(250)) { it / 12 } }
            ) {
                PlaceholderScreen(label = "⚙️ Settings\n\nComing soon")
            }

            composable(
                route = Routes.FOREST,
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(350)) { it / 12 } },
                exitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(250)) { -it / 12 } }
            ) {
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
                    },
                    onDismiss = {
                        // User swiped the animal away — skip the break and return home
                        navController.popBackStack(Routes.HOME, inclusive = false)
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

        AnimatedVisibility(
            visible = showBottomNav || predictiveNavReveal.value > 0f,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .offset(y = if (showBottomNav) 0.dp else 96.dp * (1f - predictiveNavReveal.value)),
            enter = slideInVertically(
                animationSpec = tween(360, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                animationSpec = tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                targetOffsetY = { it }
            ) + fadeOut(animationSpec = tween(180))
        ) {
            HavenBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
            )
        }
    }
}

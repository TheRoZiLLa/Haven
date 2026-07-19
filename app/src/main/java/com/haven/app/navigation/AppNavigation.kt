package com.haven.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.haven.app.feature.home.HomeScreen
import com.haven.app.feature.home.RedesignCanvasScreen
import com.haven.app.ui.theme.WarmWhite
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.haven.app.core.data.HomePreferencesRepository
import com.haven.app.feature.home.HomeViewModel
import com.haven.app.feature.home.HomeViewModelFactory

/**
 * App-level navigation routes.
 */
object Routes {
    const val HOME      = "home"
    const val FOREST    = "forest"
    const val MISSIONS  = "missions"
    const val SHOP      = "shop"
    const val PROFILE   = "profile"
    const val TIMER     = "timer"
    const val SETTINGS  = "settings"
}

/**
 * Root navigation host with scaffold + floating bottom navigation.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val repository = androidx.compose.runtime.remember { HomePreferencesRepository(context) }

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
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
                )
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
                route = "${Routes.TIMER}/{seedId}/{focusTime}/{breakTime}",
                arguments = listOf(
                    navArgument("seedId") { type = NavType.StringType },
                    navArgument("focusTime") { type = NavType.IntType },
                    navArgument("breakTime") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val seedId = backStackEntry.arguments?.getString("seedId") ?: "oak"
                val focusTime = backStackEntry.arguments?.getInt("focusTime") ?: 25
                val breakTime = backStackEntry.arguments?.getInt("breakTime") ?: 5
                PlaceholderScreen(label = "⏱ Timer\nSeed: $seedId\nFocus: $focusTime min\nBreak: $breakTime min")
            }
        }
    }
}

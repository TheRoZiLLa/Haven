package com.haven.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.haven.app.navigation.AppNavigation
import com.haven.app.navigation.NavigationTarget
import com.haven.app.navigation.Routes
import com.haven.app.ui.theme.HavenTheme

class MainActivity : ComponentActivity() {
    
    companion object {
        const val ACTION_BREAK_SELECTION = "com.haven.app.ACTION_BREAK_SELECTION"
        const val EXTRA_GOTO_BREAK_SELECTION = "EXTRA_GOTO_BREAK_SELECTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            HavenTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            val isBreakSelection = intent.action == ACTION_BREAK_SELECTION || 
                    intent.getBooleanExtra(EXTRA_GOTO_BREAK_SELECTION, false)
            if (isBreakSelection) {
                val breakTime = com.haven.app.feature.timer.FocusService.breakTimeMinutes
                NavigationTarget.pendingRoute = "${Routes.BREAK_SELECTION}/$breakTime"
            }
        }
    }
}
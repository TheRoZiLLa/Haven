package com.haven.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.haven.app.navigation.AppNavigation
import com.haven.app.navigation.NavigationTarget
import com.haven.app.navigation.Routes
import com.haven.app.ui.theme.HavenTheme

class MainActivity : ComponentActivity() {
    
    companion object {
        const val ACTION_BREAK_SELECTION = "com.haven.app.ACTION_BREAK_SELECTION"
        const val EXTRA_GOTO_BREAK_SELECTION = "EXTRA_GOTO_BREAK_SELECTION"
        
        var isAppInForeground = false
    }

    override fun onStart() {
        super.onStart()
        isAppInForeground = true
    }

    override fun onStop() {
        super.onStop()
        isAppInForeground = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val repository = remember { com.haven.app.core.data.OnboardingRepository(context.applicationContext) }
            val languageState = repository.appLanguageFlow.collectAsState(initial = "en")
            val language = languageState.value

            val wrappedContext = remember(language) {
                com.haven.app.core.util.LocaleHelper.wrapContext(context, language)
            }

            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalContext provides wrappedContext,
                androidx.activity.compose.LocalActivityResultRegistryOwner provides (context as ComponentActivity)
            ) {
                HavenTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppNavigation()
                    }
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
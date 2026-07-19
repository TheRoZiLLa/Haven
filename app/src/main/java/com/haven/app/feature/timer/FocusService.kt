package com.haven.app.feature.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.haven.app.MainActivity
import com.haven.app.navigation.NavigationTarget
import com.haven.app.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Foreground Service that handles the Focus timer session.
 * Exposes active timer state to the UI via static Compose states and flow streams.
 */
class FocusService : Service() {

    companion object {
        const val CHANNEL_ID = "haven_focus_timer_channel"
        const val NOTIFICATION_ID = 1001
        const val COMPLETE_NOTIFICATION_ID = 1002

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_CANCEL = "ACTION_CANCEL"
        const val ACTION_FORCE_COMPLETE = "ACTION_FORCE_COMPLETE"

        const val EXTRA_FOCUS_MINUTES = "EXTRA_FOCUS_MINUTES"
        const val EXTRA_BREAK_MINUTES = "EXTRA_BREAK_MINUTES"
        const val EXTRA_SEED_ID = "EXTRA_SEED_ID"
        const val EXTRA_DEBUG_10S = "EXTRA_DEBUG_10S"

        // Static states for legacy/alternative access
        var isRunning by mutableStateOf(false)
        var isPaused by mutableStateOf(false)
        var remainingSeconds by mutableStateOf(0)
        var totalSeconds by mutableStateOf(0)
        var seedId by mutableStateOf("oak")
        var breakTimeMinutes by mutableStateOf(5)

        // Flow-based states for 100% reliable Compose lifecycle-aware observation
        private val _remainingSecondsFlow = MutableStateFlow(0)
        val remainingSecondsFlow: StateFlow<Int> = _remainingSecondsFlow.asStateFlow()

        private val _isRunningFlow = MutableStateFlow(false)
        val isRunningFlow: StateFlow<Boolean> = _isRunningFlow.asStateFlow()

        private val _isPausedFlow = MutableStateFlow(false)
        val isPausedFlow: StateFlow<Boolean> = _isPausedFlow.asStateFlow()

        private val _totalSecondsFlow = MutableStateFlow(0)
        val totalSecondsFlow: StateFlow<Int> = _totalSecondsFlow.asStateFlow()

        /**
         * Reusable helper to format seconds to "MM:SS"
         */
        fun formatRemainingTime(seconds: Int): String {
            val mins = seconds / 60
            val secs = seconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

        fun start(context: Context, seed: String, minutes: Int, breakMins: Int, isDebug10s: Boolean) {
            val intent = Intent(context, FocusService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SEED_ID, seed)
                putExtra(EXTRA_FOCUS_MINUTES, minutes)
                putExtra(EXTRA_BREAK_MINUTES, breakMins)
                putExtra(EXTRA_DEBUG_10S, isDebug10s)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pause(context: Context) {
            val intent = Intent(context, FocusService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun resume(context: Context) {
            val intent = Intent(context, FocusService::class.java).apply {
                action = ACTION_RESUME
            }
            context.startService(intent)
        }

        fun cancel(context: Context) {
            val intent = Intent(context, FocusService::class.java).apply {
                action = ACTION_CANCEL
            }
            context.startService(intent)
        }

        fun forceComplete(context: Context) {
            val intent = Intent(context, FocusService::class.java).apply {
                action = ACTION_FORCE_COMPLETE
            }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val minutes = intent.getIntExtra(EXTRA_FOCUS_MINUTES, 25)
                val breakMins = intent.getIntExtra(EXTRA_BREAK_MINUTES, 5)
                val seed = intent.getStringExtra(EXTRA_SEED_ID) ?: "oak"
                val isDebug10s = intent.getBooleanExtra(EXTRA_DEBUG_10S, false)
                startTimer(seed, minutes, breakMins, isDebug10s)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_CANCEL -> cancelTimer()
            ACTION_FORCE_COMPLETE -> forceCompleteTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(seed: String, minutes: Int, breakMins: Int, isDebug10s: Boolean) {
        if (isRunning) return
        
        isRunning = true
        _isRunningFlow.value = true
        isPaused = false
        _isPausedFlow.value = false
        seedId = seed
        breakTimeMinutes = breakMins
        
        if (isDebug10s) {
            totalSeconds = 10
            remainingSeconds = 10
        } else {
            totalSeconds = minutes * 60
            remainingSeconds = totalSeconds
        }
        
        _totalSecondsFlow.value = totalSeconds
        _remainingSecondsFlow.value = remainingSeconds

        startForeground(NOTIFICATION_ID, buildTimerNotification())
        
        startTickerLoop()
    }

    private fun startTickerLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (remainingSeconds > 0) {
                delay(1000)
                if (!isPaused) {
                    remainingSeconds--
                    _remainingSecondsFlow.value = remainingSeconds
                    notificationManager.notify(NOTIFICATION_ID, buildTimerNotification())
                }
            }
            onTimerFinished()
        }
    }

    private fun pauseTimer() {
        if (!isRunning || isPaused) return
        isPaused = true
        _isPausedFlow.value = true
        notificationManager.notify(NOTIFICATION_ID, buildTimerNotification())
    }

    private fun resumeTimer() {
        if (!isRunning || !isPaused) return
        isPaused = false
        _isPausedFlow.value = false
        notificationManager.notify(NOTIFICATION_ID, buildTimerNotification())
    }

    private fun cancelTimer() {
        stopTicker()
        stopSelf()
    }

    private fun forceCompleteTimer() {
        if (!isRunning) return
        remainingSeconds = 0
        _remainingSecondsFlow.value = 0
        onTimerFinished()
    }

    private fun stopTicker() {
        timerJob?.cancel()
        timerJob = null
        isRunning = false
        _isRunningFlow.value = false
        isPaused = false
        _isPausedFlow.value = false
        remainingSeconds = 0
        _remainingSecondsFlow.value = 0
        totalSeconds = 0
        _totalSecondsFlow.value = 0
    }

    private fun onTimerFinished() {
        stopTicker()

        // Open Animal Overlay on top of all apps via OverlayService
        OverlayService.start(this)

        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HAVEN Session Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live remaining countdown during Focus Session"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildTimerNotification(): Notification {
        val timeStr = "${formatRemainingTime(remainingSeconds)} Remaining"

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseActionIntent = Intent(this, FocusService::class.java).apply {
            action = if (isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelActionIntent = Intent(this, FocusService::class.java).apply {
            action = ACTION_CANCEL
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            2,
            cancelActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseLabel = if (isPaused) "Resume" else "Pause"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (isPaused) "Focus Session (Paused)" else "Focus Session")
            .setContentText(timeStr)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_pause, pauseLabel, pausePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelPendingIntent)
            .build()
    }


    override fun onDestroy() {
        stopTicker()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

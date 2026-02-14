package com.fahim.chesstimer

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fahim.chesstimer.ui.screen.SettingsScreen
import com.fahim.chesstimer.ui.screen.TimerScreen
import com.fahim.chesstimer.ui.theme.ChessTimerTheme
import com.fahim.chesstimer.ui.theme.ThemeStyle

class MainActivity : ComponentActivity() {

    private val viewModel: ChessTimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Load persisted theme preference
        val prefs = getSharedPreferences("chess_timer_prefs", Context.MODE_PRIVATE)
        val savedTheme = prefs.getString("theme", ThemeStyle.GRANDMASTER.name)
        val initialTheme = try {
            ThemeStyle.valueOf(savedTheme ?: ThemeStyle.GRANDMASTER.name)
        } catch (_: IllegalArgumentException) {
            ThemeStyle.GRANDMASTER
        }

        setContent {
            var themeStyle by remember { mutableStateOf(initialTheme) }
            var showSettings by remember { mutableStateOf(false) }

            ChessTimerTheme(themeStyle = themeStyle) {
                if (showSettings) {
                    SettingsScreen(
                        viewModel = viewModel,
                        currentTheme = themeStyle,
                        onThemeChanged = { newTheme ->
                            themeStyle = newTheme
                            prefs.edit().putString("theme", newTheme.name).apply()
                        },
                        onBack = { showSettings = false }
                    )
                } else {
                    TimerScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { showSettings = true }
                    )
                }
            }
        }
    }
}

package com.fahim.chesstimer

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var player1TimeView: TextView
    private lateinit var player2TimeView: TextView
    private lateinit var player1Layout: LinearLayout
    private lateinit var player2Layout: LinearLayout
    private lateinit var pauseButton: ImageView
    private lateinit var resetButton: ImageView
    private lateinit var settingsButton: ImageView

    private val viewModel: ChessTimerViewModel by viewModels()
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        volumeControlStream = AudioManager.STREAM_MUSIC

        soundManager = SoundManager(this)

        // Initialize views
        player1TimeView = findViewById(R.id.player1_time)
        player2TimeView = findViewById(R.id.player2_time)
        player1Layout = findViewById(R.id.player1_timer)
        player2Layout = findViewById(R.id.player2_timer)
        pauseButton = findViewById(R.id.pause_button)
        resetButton = findViewById(R.id.reset_button)
        settingsButton = findViewById(R.id.settings_button)

        // Click listeners delegate to ViewModel
        player1Layout.setOnClickListener {
            val state = viewModel.gameState.value
            if (!state.isGameStarted) {
                viewModel.startGame()
            } else {
                viewModel.switchTurn(true)
            }
        }
        player2Layout.setOnClickListener {
            val state = viewModel.gameState.value
            if (!state.isGameStarted) {
                viewModel.startGame()
            } else {
                viewModel.switchTurn(false)
            }
        }

        pauseButton.setOnClickListener {
            val state = viewModel.gameState.value
            if (!state.isGameStarted) {
                viewModel.startGame()
            } else if (state.isGameRunning && !state.isGamePaused) {
                viewModel.pauseGame()
            } else if (state.isGamePaused) {
                viewModel.resumeGame()
            }
        }
        resetButton.setOnClickListener { viewModel.resetGame() }
        settingsButton.setOnClickListener { showSettingsDialog() }

        // Observe game state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { state ->
                    updateUI(state)
                }
            }
        }

        // Observe one-shot events
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is GameEvent.PlayTapSound -> soundManager.playTap()
                        is GameEvent.ShowGameOver -> {
                            soundManager.playGameOver()
                            Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(state: GameState) {
        player1TimeView.text = formatTime(state.player1TimeLeft)
        player2TimeView.text = formatTime(state.player2TimeLeft)

        if (!state.isGameStarted || state.isGamePaused || !state.isGameRunning) {
            pauseButton.setImageResource(R.drawable.baseline_play_arrow_24)
        } else {
            pauseButton.setImageResource(R.drawable.baseline_pause_24)
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun showSettingsDialog() {
        val state = viewModel.gameState.value
        if (state.isGameStarted && state.isGameRunning && !state.isGamePaused) {
            Toast.makeText(this, "Pause the game before changing settings", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Timer Settings")
            .create()

        val timeControlGroup = dialogView.findViewById<RadioGroup>(R.id.time_control_group)
        val customTimeLayout = dialogView.findViewById<LinearLayout>(R.id.custom_time_layout)
        val saveButton = dialogView.findViewById<Button>(R.id.save_settings_button)

        // Set up radio group listener
        timeControlGroup.setOnCheckedChangeListener { _, checkedId ->
            customTimeLayout.visibility =
                if (checkedId == R.id.control_custom) View.VISIBLE else View.GONE
        }
        // Set initial selection
        timeControlGroup.check(R.id.control_5_0)

        saveButton.setOnClickListener {
            val minutes: Int
            val seconds: Int
            val increment: Int
            when (timeControlGroup.checkedRadioButtonId) {
                R.id.control_5_0 -> {
                    minutes = 5
                    seconds = 0
                    increment = 0
                }

                R.id.control_10_5 -> {
                    minutes = 10
                    seconds = 0
                    increment = 5
                }

                R.id.control_30_0 -> {
                    minutes = 30
                    seconds = 0
                    increment = 0
                }

                R.id.control_custom -> {
                    minutes = dialogView.findViewById<EditText>(R.id.custom_minutes).text.toString()
                        .toIntOrNull() ?: 5
                    seconds = dialogView.findViewById<EditText>(R.id.custom_seconds).text.toString()
                        .toIntOrNull() ?: 0
                    increment =
                        dialogView.findViewById<EditText>(R.id.custom_increment).text.toString()
                            .toIntOrNull() ?: 0
                    val minBlack =
                        dialogView.findViewById<EditText>(R.id.custom_minutes_black).text.toString()
                            .toIntOrNull() ?: 5
                    val secBlack =
                        dialogView.findViewById<EditText>(R.id.custom_seconds_black).text.toString()
                            .toIntOrNull() ?: 0
                    val incBlack =
                        dialogView.findViewById<EditText>(R.id.custom_increment_black).text.toString()
                            .toIntOrNull() ?: 0
                    // Validate input
                    if (minutes < 0 || seconds < 0 || increment < 0 || (minutes == 0 && seconds == 0)) {
                        Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (minBlack < 0 || secBlack < 0 || incBlack < 0 || (minBlack == 0 && secBlack == 0)) {
                        Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    // Apply settings via ViewModel
                    viewModel.applySettings(
                        p1Time = (minutes * 60 + seconds) * 1000L,
                        p2Time = (minBlack * 60 + secBlack) * 1000L,
                        increment = increment * 1000L
                    )
                    dialog.dismiss()
                    return@setOnClickListener
                }

                else -> {
                    minutes = 5
                    seconds = 0
                    increment = 0
                }
            }

            // Validate input
            if (minutes < 0 || seconds < 0 || increment < 0 || (minutes == 0 && seconds == 0)) {
                Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Apply settings via ViewModel
            viewModel.applySettings(
                p1Time = (minutes * 60 + seconds) * 1000L,
                p2Time = (minutes * 60 + seconds) * 1000L,
                increment = increment * 1000L
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroy() {
        soundManager.release()
        super.onDestroy()
    }
}

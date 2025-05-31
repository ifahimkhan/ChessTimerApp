package com.fahim.chesstimer

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var player1Timer: CountDownTimer
    private lateinit var player2Timer: CountDownTimer
    private lateinit var player1TimeView: TextView
    private lateinit var player2TimeView: TextView
    private lateinit var player1Layout: LinearLayout
    private lateinit var player2Layout: LinearLayout
    private lateinit var pauseButton: ImageView
    private lateinit var resetButton: ImageView
    private lateinit var settingsButton: ImageView

    private var player1TimeLeft: Long = 5 * 60 * 1000 // 5 minutes in milliseconds
    private var player2TimeLeft: Long = 5 * 60 * 1000
    private var timeIncrement: Long = 0 // in milliseconds
    private var isPlayer1Turn = true
    private var isGameRunning = true
    private var isGamePaused = false
    private var timerInterval: Long = 100 // Update interval in ms

    private var toneGenerator: ToneGenerator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: RuntimeException) {
            Log.e("ToneGenerator", "Failed to initialize ToneGenerator", e)
            // Fallback to MediaPlayer or show error
        }
        player1Timer = object : CountDownTimer(player1TimeLeft, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                player1TimeLeft = millisUntilFinished
                updateTimeDisplay()
            }

            override fun onFinish() {
                player1TimeLeft = 0
                updateTimeDisplay()
                gameOver(false) // Player 1 (White) loses on time
            }
        }
        player2Timer = object : CountDownTimer(player2TimeLeft, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                player2TimeLeft = millisUntilFinished
                updateTimeDisplay()
            }

            override fun onFinish() {
                player2TimeLeft = 0
                updateTimeDisplay()
                gameOver(true) // Player 2 (Black) loses on time
            }
        }


        // Initialize views
        player1TimeView = findViewById(R.id.player1_time)
        player2TimeView = findViewById(R.id.player2_time)
        player1Layout = findViewById(R.id.player1_timer)
        player2Layout = findViewById(R.id.player2_timer)
        pauseButton = findViewById(R.id.pause_button)
        resetButton = findViewById(R.id.reset_button)
        settingsButton = findViewById(R.id.settings_button)


        // Set initial time display
        updateTimeDisplay()

        player1Layout.setOnClickListener { switchTurn(true) }
        player2Layout.setOnClickListener { switchTurn(false) }

        pauseButton.setOnClickListener {
            if (isGameRunning && !isGamePaused) {
                pauseGame()
            } else if (isGamePaused) {
                resumeGame()
            }
        }
        resetButton.setOnClickListener { resetGame() }
        settingsButton.setOnClickListener { showSettingsDialog() }

//        highlightActivePlayer()


    }

    private fun highlightActivePlayer() {
        if (isPlayer1Turn) {
            player1Layout.setBackgroundColor(ContextCompat.getColor(this, R.color.active_player))
            player2Layout.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_player))
        } else {
            player1Layout.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive_player))
            player2Layout.setBackgroundColor(ContextCompat.getColor(this, R.color.active_player))
        }
    }

    private fun showSettingsDialog() {
        if (isGameRunning && !isGamePaused) {
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
            val min_black: Int
            val sec_black:Int
            val inc_black:Int
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
                    min_black = dialogView.findViewById<EditText>(R.id.custom_minutes_black).text.toString()
                        .toIntOrNull() ?: 5
                    sec_black = dialogView.findViewById<EditText>(R.id.custom_seconds_black).text.toString()
                        .toIntOrNull() ?: 0
                    inc_black =
                        dialogView.findViewById<EditText>(R.id.custom_increment_black).text.toString()
                            .toIntOrNull() ?: 0
                    // Validate input
                    if (minutes < 0 || seconds < 0 || increment < 0 || (minutes == 0 && seconds == 0)) {
                        Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (min_black < 0 || sec_black < 0 || inc_black < 0 || (min_black == 0 && sec_black == 0)) {
                        Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    // Apply settings
                    player1TimeLeft = (minutes * 60 + seconds) * 1000L
                    player2TimeLeft = (min_black * 60 + sec_black) * 1000L
                    timeIncrement = increment * 1000L

                    updateTimeDisplay()
                    dialog.dismiss()
                  return@setOnClickListener

                }

                else -> {
                    minutes = 5
                    seconds = 0
                    increment = 0
                    min_black = 5
                }
            }

            // Validate input
            if (minutes < 0 || seconds < 0 || increment < 0 || (minutes == 0 && seconds == 0)) {
                Toast.makeText(this, "Invalid time settings", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Apply settings
            player1TimeLeft = (minutes * 60 + seconds) * 1000L
            player2TimeLeft = (minutes * 60 + seconds) * 1000L
            timeIncrement = increment * 1000L

            updateTimeDisplay()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun resetGame() {
        if (isGameRunning) {
            if (isPlayer1Turn) {
                player1Timer.cancel()
            } else {
                player2Timer.cancel()
            }
        }

        // Reset to initial state
        player1TimeLeft = 5 * 60 * 1000
        player2TimeLeft = 5 * 60 * 1000
        timeIncrement = 0
        isPlayer1Turn = true
        isGamePaused = false

        updateTimeDisplay()
//        highlightActivePlayer()
        pauseButton.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun resumeGame() {
        if (isGamePaused) {
            if (isPlayer1Turn) {
                startPlayer1Timer()
            } else {
                startPlayer2Timer()
            }
            isGamePaused = false
            pauseButton.setImageResource(R.drawable.baseline_pause_24)
        }
    }

    private fun pauseGame() {
        if (isGameRunning) {
            if (isPlayer1Turn) {
                player1Timer.cancel()
            } else {
                player2Timer.cancel()
            }
            isGamePaused = true
            pauseButton.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun updateTimeDisplay() {
        player1TimeView.text = formatTime(player1TimeLeft)
        player2TimeView.text = formatTime(player2TimeLeft)
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }


    private fun switchTurn(isPlayer1Pressed: Boolean) {
        Log.e("TAG", "switchTurn: $isPlayer1Pressed")
        if (!isGameRunning || isGamePaused) return
        // Only allow the active player to press their clock
        if ((isPlayer1Turn && !isPlayer1Pressed) || (!isPlayer1Turn && isPlayer1Pressed)) {
            return
        }
        if (isPlayer1Turn) {
            player1Timer.cancel()
            player2TimeLeft += timeIncrement // Add increment
            startPlayer2Timer()
        } else {
            player2Timer.cancel()
            player1TimeLeft += timeIncrement // Add increment
            startPlayer1Timer()
        }

        isPlayer1Turn = !isPlayer1Turn
//        highlightActivePlayer()
    }

    private fun startPlayer1Timer() {
        player1Timer = object : CountDownTimer(player1TimeLeft, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                player1TimeLeft = millisUntilFinished
                updateTimeDisplay()
            }

            override fun onFinish() {
                player1TimeLeft = 0
                updateTimeDisplay()
                gameOver(false) // Player 1 (White) loses on time
            }
        }
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT)
        player1Timer.start()
    }

    private fun startPlayer2Timer() {
        player2Timer = object : CountDownTimer(player2TimeLeft, timerInterval) {
            override fun onTick(millisUntilFinished: Long) {
                player2TimeLeft = millisUntilFinished
                updateTimeDisplay()
            }

            override fun onFinish() {
                player2TimeLeft = 0
                updateTimeDisplay()
                gameOver(true) // Player 2 (Black) loses on time
            }
        }
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT)
        player2Timer.start()
    }


    private fun gameOver(whiteWins: Boolean) {
        isGameRunning = false

        // Play sound
        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
        // Show winner
        val winner = if (whiteWins) "White wins!" else "Black wins!"
        Toast.makeText(this, "Time out! $winner", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        toneGenerator?.release()
        toneGenerator = null
        super.onDestroy()
    }
}
package com.fahim.chesstimer.ui.screen

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.chesstimer.ChessTimerViewModel
import com.fahim.chesstimer.GameEvent
import com.fahim.chesstimer.R
import com.fahim.chesstimer.SoundManager
import com.fahim.chesstimer.ui.component.AnimatedCounter
import com.fahim.chesstimer.ui.theme.LocalChessTimerColors
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: ChessTimerViewModel,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val extendedColors = LocalChessTimerColors.current

    // Derived state: format timer text only when the displayed second changes
    val player1TimeText by remember {
        derivedStateOf { formatTime(state.player1TimeLeft) }
    }
    val player2TimeText by remember {
        derivedStateOf { formatTime(state.player2TimeLeft) }
    }

    // Derived minutes/seconds for animated counter (only change on second boundaries)
    val player1Minutes by remember {
        derivedStateOf { ((state.player1TimeLeft / 1000) / 60 % 60).toInt() }
    }
    val player1Seconds by remember {
        derivedStateOf { ((state.player1TimeLeft / 1000) % 60).toInt() }
    }
    val player2Minutes by remember {
        derivedStateOf { ((state.player2TimeLeft / 1000) / 60 % 60).toInt() }
    }
    val player2Seconds by remember {
        derivedStateOf { ((state.player2TimeLeft / 1000) % 60).toInt() }
    }

    // Derived state for control bar icon (avoids recomposing controls on every tick)
    val showPause by remember {
        derivedStateOf {
            state.isGameStarted && state.isGameRunning && !state.isGamePaused
        }
    }

    // Sound manager with lifecycle-aware cleanup
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    // One-shot event handling
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.PlayTapSound -> soundManager.playTap()
                is GameEvent.ShowGameOver -> {
                    soundManager.playGameOver()
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    // Remembered click handlers to avoid lambda allocation on every recomposition
    val onPlayer1Click = remember<() -> Unit> {
        {
            val s = viewModel.gameState.value
            if (!s.isGameStarted) viewModel.startGame()
            else viewModel.switchTurn(true)
        }
    }
    val onPlayer2Click = remember<() -> Unit> {
        {
            val s = viewModel.gameState.value
            if (!s.isGameStarted) viewModel.startGame()
            else viewModel.switchTurn(false)
        }
    }
    val onPlayPauseClick = remember<() -> Unit> {
        {
            val s = viewModel.gameState.value
            when {
                !s.isGameStarted -> viewModel.startGame()
                s.isGameRunning && !s.isGamePaused -> viewModel.pauseGame()
                s.isGamePaused -> viewModel.resumeGame()
            }
        }
    }
    val onResetClick = remember<() -> Unit> { { viewModel.resetGame() } }
    val onSettingsClick = remember<() -> Unit> {
        {
            val s = viewModel.gameState.value
            if (s.isGameStarted && s.isGameRunning && !s.isGamePaused) {
                Toast.makeText(context, "Pause the game before changing settings", Toast.LENGTH_SHORT).show()
            } else {
                onNavigateToSettings()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(extendedColors.controlBarBackground)
        ) {
            // Player 1 (White) - rotated 180 degrees for opponent viewing
            PlayerTimerArea(
                modifier = Modifier
                    .weight(1f)
                    .rotate(180f),
                timeText = player1TimeText,
                minutes = player1Minutes,
                seconds = player1Seconds,
                playerLabel = "WHITE",
                moves = state.player1Moves,
                increment = state.timeIncrement,
                isActive = state.isPlayer1Turn && state.isGameStarted
                        && state.isGameRunning && !state.isGamePaused,
                isWhitePlayer = true,
                isLowTime = state.player1TimeLeft < 30_000 && state.isGameStarted,
                onClick = onPlayer1Click
            )

            // Control bar
            ControlBar(
                showPause = showPause,
                onPlayPause = onPlayPauseClick,
                onReset = onResetClick,
                onSettings = onSettingsClick
            )

            // Player 2 (Black)
            PlayerTimerArea(
                modifier = Modifier.weight(1f),
                timeText = player2TimeText,
                minutes = player2Minutes,
                seconds = player2Seconds,
                playerLabel = "BLACK",
                moves = state.player2Moves,
                increment = state.timeIncrement,
                isActive = !state.isPlayer1Turn && state.isGameStarted
                        && state.isGameRunning && !state.isGamePaused,
                isWhitePlayer = false,
                isLowTime = state.player2TimeLeft < 30_000 && state.isGameStarted,
                onClick = onPlayer2Click
            )
        }
    }
}

// All parameters are stable primitives/Strings -> composable is skippable
@Composable
private fun PlayerTimerArea(
    timeText: String,
    minutes: Int,
    seconds: Int,
    playerLabel: String,
    moves: Int,
    increment: Long,
    isActive: Boolean,
    isWhitePlayer: Boolean,
    isLowTime: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalChessTimerColors.current

    val backgroundColor = if (isWhitePlayer) {
        extendedColors.whitePlayerBackground
    } else {
        extendedColors.blackPlayerBackground
    }

    val contentColor = if (isWhitePlayer) {
        extendedColors.whitePlayerText
    } else {
        extendedColors.blackPlayerText
    }

    // Smooth border color transition on turn switch
    val borderColor by animateColorAsState(
        targetValue = if (isActive) extendedColors.activeAccent else Color.Transparent,
        animationSpec = tween(300),
        label = "borderColor"
    )

    // Timer text color shifts to warning red when low on time
    val timerColor by animateColorAsState(
        targetValue = if (isLowTime) extendedColors.lowTimeWarning else contentColor,
        animationSpec = tween(300),
        label = "timerColor"
    )

    // Pulsing alpha when low time AND active (draws attention)
    val infiniteTransition = rememberInfiniteTransition(label = "lowTimePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLowTime && isActive) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(
                if (isActive) {
                    Modifier.border(width = 4.dp, color = borderColor)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .semantics {
                contentDescription =
                    "$playerLabel timer: $timeText${if (isActive) ", active, your turn" else ""}"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Player label
            Text(
                text = playerLabel,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timer digits - animated odometer style
            Row(verticalAlignment = Alignment.CenterVertically) {
                val timerStyle = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
                val animatedTimerColor = timerColor.copy(alpha = pulseAlpha)

                AnimatedCounter(
                    targetValue = minutes,
                    style = timerStyle,
                    color = animatedTimerColor,
                    animationDurationMs = 300,
                    minDigits = 2
                )
                Text(
                    text = ":",
                    style = timerStyle,
                    color = animatedTimerColor
                )
                AnimatedCounter(
                    targetValue = seconds,
                    style = timerStyle,
                    color = animatedTimerColor,
                    animationDurationMs = 300,
                    minDigits = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Move counter + increment badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (moves > 0) {
                    Text(
                        text = "Move $moves",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.4f)
                    )
                }

                if (increment > 0) {
                    Text(
                        text = "+${increment / 1000}s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = extendedColors.activeAccent.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlBar(
    showPause: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalChessTimerColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = extendedColors.controlBarBackground,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (showPause) R.drawable.baseline_pause_24
                        else R.drawable.baseline_play_arrow_24
                    ),
                    contentDescription = if (showPause) "Pause game" else "Start or resume game",
                    tint = extendedColors.controlBarContent,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onReset,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_restore_24),
                    contentDescription = "Reset game",
                    tint = extendedColors.controlBarContent,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = onSettings,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_settings_24),
                    contentDescription = "Open settings",
                    tint = extendedColors.controlBarContent,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

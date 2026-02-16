package com.fahim.chesstimer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChessTimerViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var timerJob: Job? = null
    private val timerInterval = 100L

    // Remember configured settings for reset
    private var configuredP1Time = 5 * 60 * 1000L
    private var configuredP2Time = 5 * 60 * 1000L
    private var configuredIncrement = 0L

    fun startGame() {
        _gameState.update { it.copy(isGameStarted = true, isPlayer1Turn = true) }
        startTimer(isPlayer1 = true)
        _events.trySend(GameEvent.PlayTapSound)
    }

    fun switchTurn(isPlayer1Pressed: Boolean) {
        val state = _gameState.value
        Log.e("TAG", "switchTurn: $isPlayer1Pressed")
        if (!state.isGameStarted || !state.isGameRunning || state.isGamePaused) return
        // Only allow the active player to press their clock
        if ((state.isPlayer1Turn && !isPlayer1Pressed) || (!state.isPlayer1Turn && isPlayer1Pressed)) {
            return
        }

        timerJob?.cancel()

        if (state.isPlayer1Turn) {
            // Player 1 pressed — they completed their move, give them increment
            _gameState.update {
                it.copy(
                    player1TimeLeft = it.player1TimeLeft + it.timeIncrement,
                    isPlayer1Turn = false,
                    player1Moves = it.player1Moves + 1
                )
            }
            startTimer(isPlayer1 = false)
        } else {
            // Player 2 pressed — they completed their move, give them increment
            _gameState.update {
                it.copy(
                    player2TimeLeft = it.player2TimeLeft + it.timeIncrement,
                    isPlayer1Turn = true,
                    player2Moves = it.player2Moves + 1
                )
            }
            startTimer(isPlayer1 = true)
        }

        _events.trySend(GameEvent.PlayTapSound)
    }

    fun pauseGame() {
        val state = _gameState.value
        if (state.isGameStarted && state.isGameRunning) {
            timerJob?.cancel()
            _gameState.update { it.copy(isGamePaused = true) }
        }
    }

    fun resumeGame() {
        val state = _gameState.value
        if (state.isGamePaused) {
            _gameState.update { it.copy(isGamePaused = false) }
            startTimer(isPlayer1 = state.isPlayer1Turn)
        }
    }

    fun resetGame() {
        timerJob?.cancel()
        _gameState.value = GameState(
            player1TimeLeft = configuredP1Time,
            player2TimeLeft = configuredP2Time,
            timeIncrement = configuredIncrement
        )
    }

    fun applySettings(p1Time: Long, p2Time: Long, increment: Long) {
        timerJob?.cancel()
        configuredP1Time = p1Time
        configuredP2Time = p2Time
        configuredIncrement = increment
        _gameState.value = GameState(
            player1TimeLeft = p1Time,
            player2TimeLeft = p2Time,
            timeIncrement = increment
        )
    }

    private fun startTimer(isPlayer1: Boolean) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(timerInterval)
                val current = _gameState.value
                if (isPlayer1) {
                    val newTime = current.player1TimeLeft - timerInterval
                    if (newTime <= 0) {
                        _gameState.update { it.copy(player1TimeLeft = 0, isGameRunning = false) }
                        _events.trySend(GameEvent.ShowGameOver("Time out! Black wins!"))
                        break
                    }
                    _gameState.update { it.copy(player1TimeLeft = newTime) }
                } else {
                    val newTime = current.player2TimeLeft - timerInterval
                    if (newTime <= 0) {
                        _gameState.update { it.copy(player2TimeLeft = 0, isGameRunning = false) }
                        _events.trySend(GameEvent.ShowGameOver("Time out! White wins!"))
                        break
                    }
                    _gameState.update { it.copy(player2TimeLeft = newTime) }
                }
            }
        }
    }
}

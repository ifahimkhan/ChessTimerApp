package com.fahim.chesstimer

data class GameState(
    val player1TimeLeft: Long = 5 * 60 * 1000L,
    val player2TimeLeft: Long = 5 * 60 * 1000L,
    val timeIncrement: Long = 0L,
    val isPlayer1Turn: Boolean = true,
    val isGameStarted: Boolean = false,
    val isGameRunning: Boolean = true,
    val isGamePaused: Boolean = false,
    val player1Moves: Int = 0,
    val player2Moves: Int = 0
)

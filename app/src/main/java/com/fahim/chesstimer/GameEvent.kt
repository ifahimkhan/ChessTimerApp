package com.fahim.chesstimer

sealed class GameEvent {
    object PlayTapSound : GameEvent()
    data class ShowGameOver(val message: String) : GameEvent()
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

sealed interface InvokerEventState {
    data class GameOver(val gameTime: Int) : InvokerEventState
    data object ConnectionLost : InvokerEventState
}
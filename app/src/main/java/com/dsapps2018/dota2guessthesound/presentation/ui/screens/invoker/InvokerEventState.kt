package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

sealed interface InvokerEventState {
    data object GameOver : InvokerEventState
    data object ConnectionLost : InvokerEventState
}
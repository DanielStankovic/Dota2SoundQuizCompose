package com.dsapps2018.dota2guessthesound.data.invoker

sealed interface InvokerRoundEvent {
    data class GameOver(val gameTime: Int) : InvokerRoundEvent
    data object ConnectionLost : InvokerRoundEvent
}

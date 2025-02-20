package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

sealed interface JourneyGameEvent {
    data object Correct : JourneyGameEvent
    data object Wrong: JourneyGameEvent
    data object TooManySelected: JourneyGameEvent
    data object NotEnoughSelected: JourneyGameEvent
}
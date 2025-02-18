package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel


sealed interface JourneyLevelFetchState {
    data object Loading : JourneyLevelFetchState
    data class Success(
        val data: JourneyGameModel
    ) : JourneyLevelFetchState

    data class Error(val error: String) : JourneyLevelFetchState
}
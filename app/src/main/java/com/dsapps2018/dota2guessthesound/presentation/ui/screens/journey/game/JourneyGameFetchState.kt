package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel


sealed interface JourneyGameFetchState {
    data object Loading : JourneyGameFetchState
    data class Success(
        val data: JourneyGameModel
    ) : JourneyGameFetchState

    data class Error(val error: String) : JourneyGameFetchState
}
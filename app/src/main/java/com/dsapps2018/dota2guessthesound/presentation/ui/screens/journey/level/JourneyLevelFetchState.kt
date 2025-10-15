package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import com.dsapps2018.dota2guessthesound.data.model.JourneyLevelModel


sealed interface JourneyLevelFetchState {
    data object Loading : JourneyLevelFetchState
    data class Success(
        val data: List<JourneyLevelModel>
    ) : JourneyLevelFetchState

    data class Error(val error: String) : JourneyLevelFetchState
}
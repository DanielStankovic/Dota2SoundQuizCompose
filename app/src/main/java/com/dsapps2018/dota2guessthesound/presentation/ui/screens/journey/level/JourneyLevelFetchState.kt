package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import com.dsapps2018.dota2guessthesound.data.api.response.JourneyLevelDto


sealed interface JourneyLevelFetchState {
    data object Loading : JourneyLevelFetchState
    data class Success(
        val data: List<JourneyLevelDto>
    ) : JourneyLevelFetchState

    data class Error(val error: String) : JourneyLevelFetchState
}
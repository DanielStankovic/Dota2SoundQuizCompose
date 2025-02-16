package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.presentation.navigation.JourneyGameDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JourneyGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val levelNum: Int = savedStateHandle.toRoute<JourneyGameDestination>().levelNum


    sealed interface JourneyLevelFetchState {
        data object Loading : JourneyLevelFetchState
        data class Success(
           
        ) : JourneyLevelFetchState

        data class Error(val error: String) : JourneyLevelFetchState
    }
}
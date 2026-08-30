package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRound
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundEvent
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundState
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.presentation.navigation.JourneyGameDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val journeyRound: JourneyRound,
) : ViewModel() {

    private val destination = savedStateHandle.toRoute<JourneyGameDestination>()

    val roundState: StateFlow<JourneyRoundState> = journeyRound.roundState
    val gameEvent: SharedFlow<JourneyRoundEvent> = journeyRound.events

    val levelNum: Int = destination.levelNum

    init {
        viewModelScope.launch {
            journeyRound.startRound(destination.levelNum, viewModelScope)
        }
    }

    fun toggleSoundCardState(soundId: Int) = journeyRound.toggleMark(soundId)

    fun playSound(currentSound: SoundModel?) {
        currentSound?.let { journeyRound.playSound(it) }
    }

    fun submitAnswer() = journeyRound.submit()

    fun setShowWatchAdContinueDialog(showDialog: Boolean) {
        if (!showDialog) journeyRound.dismissContinueDialog()
    }

    fun grantExtraLife() = journeyRound.grantExtraLife()

    fun resumeTimerAfterAd() = journeyRound.resumeAfterAd()

    override fun onCleared() {
        super.onCleared()
        journeyRound.clear()
    }
}

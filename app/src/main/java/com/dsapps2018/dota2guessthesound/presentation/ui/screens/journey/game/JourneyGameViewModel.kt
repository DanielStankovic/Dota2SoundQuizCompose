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
            journeyRound.startRound(
                destination.levelNum,
                viewModelScope,
                holdUntilBegin = destination.briefing,
            )
        }
    }

    fun toggleSoundCardState(soundId: Int) = journeyRound.toggleMark(soundId)

    fun playSound(currentSound: SoundModel?) {
        currentSound?.let { journeyRound.playSound(it) }
    }

    fun submitAnswer() = journeyRound.submit()

    fun beginRound() = journeyRound.beginRound()

    fun setShowWatchAdContinueDialog(showDialog: Boolean) {
        if (!showDialog) journeyRound.dismissContinueDialog()
    }

    fun continueFromExtraLifeGate(
        runRewardedAd: (onRewarded: () -> Unit, onAdDismissed: () -> Unit) -> Unit,
    ) = journeyRound.continueFromExtraLifeGate(runRewardedAd)

    fun declineExtraLifeGate() = journeyRound.declineExtraLifeGate()

    fun onHostPaused() = journeyRound.onHostPaused()

    fun onHostResumed() = journeyRound.onHostResumed()

    fun onPortraitAssistTap(heroId: Int) = journeyRound.onPortraitAssistTap(heroId)

    fun openPrimer(heroId: Int) = journeyRound.openPrimer(heroId)

    fun closePrimer() = journeyRound.closePrimer()

    fun playPrimerSound(sound: SoundModel) = journeyRound.playPrimerSound(sound)

    fun purchaseCountReveal() = journeyRound.purchaseCountReveal()

    fun unlockPrimerWithAd(
        runRewardedAd: (onRewarded: () -> Unit, onAdDismissed: () -> Unit) -> Unit,
    ) = journeyRound.unlockPrimerWithAd(runRewardedAd)

    fun grantCoinsFromAd(
        runRewardedAd: (onRewarded: () -> Unit, onAdDismissed: () -> Unit) -> Unit,
    ) = journeyRound.grantCoinsFromAd(runRewardedAd)

    fun refreshAssistGrantStatus() = journeyRound.refreshAssistGrantStatus()

    fun clearAssistTrayRequest() = journeyRound.clearAssistTrayRequest()

    override fun onCleared() {
        super.onCleared()
        journeyRound.clear()
    }
}

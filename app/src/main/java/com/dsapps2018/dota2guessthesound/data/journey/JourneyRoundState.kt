package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState
import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel
import com.dsapps2018.dota2guessthesound.data.model.JourneyLevelModel

sealed interface JourneyLevelsState {
    data object Loading : JourneyLevelsState
    data class Success(val levels: List<JourneyLevelModel>) : JourneyLevelsState
    data class Error(val message: String) : JourneyLevelsState
}

sealed interface JourneyRoundState {
    data object Idle : JourneyRoundState
    data object Loading : JourneyRoundState
    data class Ready(
        val level: Int,
        val game: JourneyGameModel,
        val hearts: Int,
        val affixUI: AffixUIState,
        val timer: TimerState?,
        val selectedMarks: Map<Int, Boolean>,
        val remainingPlays: Int?,
        val showContinueDialog: Boolean,
    ) : JourneyRoundState

    data class Error(val message: String) : JourneyRoundState
}

sealed interface JourneyRoundEvent {
    data object Correct : JourneyRoundEvent
    data object GameOver : JourneyRoundEvent
}

data class TimerState(
    val remainingMs: Long,
    val totalMs: Long,
    val isWarning: Boolean,
    val isPaused: Boolean = false,
)

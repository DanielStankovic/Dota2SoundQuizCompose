package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.enums.GameModeEnum
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.data.model.initialPlayerProgress
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playerProgressRepository: PlayerProgressRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _leaderboardUpdateStatus = MutableSharedFlow<LeaderboardUpdateState>()
    val leaderboardUpdateStatus: SharedFlow<LeaderboardUpdateState> =
        _leaderboardUpdateStatus.asSharedFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            viewModelScope.launch {
                firebaseCrashlytics.recordException(throwable)
                _leaderboardUpdateStatus.emit(
                    LeaderboardUpdateState.Error(
                        context.getString(R.string.leaderboard_update_error)
                    )
                )
            }
        }

    val progress: StateFlow<PlayerProgress> = playerProgressRepository.progress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initialPlayerProgress()
    )

    fun updateQuizScore(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            playerProgressRepository.recordQuizResult(score)
            if (score > 0) {
                leaderboardRepository.updateLeaderboard(
                    score.toDouble(),
                    GameModeEnum.QUIZ.gameCode
                )
            }
        }
    }

    fun updateInvokerScore(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            playerProgressRepository.recordInvokerResult(score)
            if (score > 0) {
                leaderboardRepository.updateLeaderboard(
                    score.toDouble(),
                    GameModeEnum.INVOKER.gameCode
                )
            }
        }
    }

    fun updateFastFingerScore(guessed: Int, total: Int, time: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val currentScore = playerProgressRepository.calculateFastFingerScore(guessed, total)
            playerProgressRepository.recordFastFingerResult(guessed, total, time)
            if (currentScore > 0.0) {
                leaderboardRepository.updateLeaderboard(
                    currentScore,
                    getGameModeFromTime(time).gameCode
                )
            }
        }
    }

    fun updateJourneyLevel(level: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            playerProgressRepository.advanceJourney(level)
        }
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double =
        playerProgressRepository.calculateFastFingerScore(guessed, total)

    private fun getGameModeFromTime(time: Int): GameModeEnum {
        return when (time) {
            30 -> GameModeEnum.FF_30
            60 -> GameModeEnum.FF_60
            90 -> GameModeEnum.FF_90
            else -> GameModeEnum.FF_30
        }
    }

    sealed interface LeaderboardUpdateState {
        data object Success : LeaderboardUpdateState
        data class Error(val error: String) : LeaderboardUpdateState
    }
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.data.model.initialPlayerProgress
import com.dsapps2018.dota2guessthesound.data.progress.ModeResult
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
    private val modeResult: ModeResult,
    playerProgressRepository: PlayerProgressRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics,
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

    fun submitQuiz(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            modeResult.submitQuiz(score)
        }
    }

    fun submitInvoker(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            modeResult.submitInvoker(score)
        }
    }

    fun submitFastFinger(guessed: Int, total: Int, time: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            modeResult.submitFastFinger(guessed, total, time)
        }
    }

    fun submitJourneyLevel(level: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            modeResult.submitJourneyLevel(level)
        }
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double =
        modeResult.calculateFastFingerScore(guessed, total)

    sealed interface LeaderboardUpdateState {
        data object Success : LeaderboardUpdateState
        data class Error(val error: String) : LeaderboardUpdateState
    }
}

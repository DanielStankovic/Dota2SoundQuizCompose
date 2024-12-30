package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.getInitialUserData
import com.dsapps2018.dota2guessthesound.data.enums.GameModeEnum
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.ScoreRepository
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import com.dsapps2018.dota2guessthesound.data.util.roundTo
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
import kotlin.math.pow

@HiltViewModel
class ScoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scoreRepository: ScoreRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    companion object {
        const val WEIGHT_MODIFIER = 2.0
    }

    private val _leaderboardUpdateStatus = MutableSharedFlow<LeaderboardUpdateState>()
    val leaderboardUpdateStatus: SharedFlow<LeaderboardUpdateState> =
        _leaderboardUpdateStatus.asSharedFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            viewModelScope.launch {
                firebaseCrashlytics.recordException(throwable)
                _leaderboardUpdateStatus.emit(
                    LeaderboardUpdateState.Error(
                        context.getString(
                            R.string.leaderboard_update_error
                        )
                    )
                )
            }
        }

    val userData: StateFlow<UserDataEntity> = scoreRepository.getUserDataFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getInitialUserData()
    )

    fun updateQuizScore(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            scoreRepository.getUserData()?.let { userData ->
                if (score > userData.quizScore) {
                    userData.quizScore = score
                }
                userData.quizPlayed++
                userData.coinValue += score
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
                leaderboardRepository.updateLeaderboard(
                    score.toDouble(),
                    GameModeEnum.QUIZ.gameCode
                )
            }
        }
    }

    fun updateInvokerScore(score: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            scoreRepository.getUserData()?.let { userData ->
                if (score > userData.invokerScore) {
                    userData.invokerScore = score
                }
                userData.invokerPlayed++
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
                leaderboardRepository.updateLeaderboard(
                    score.toDouble(),
                    GameModeEnum.INVOKER.gameCode
                )
            }
        }
    }

    fun updateFastFingerScore(guessed: Int, total: Int, time: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            scoreRepository.getUserData()?.let { userData ->
                val currentScore = calculateFastFingerScore(guessed, total)
                val savedScore = getSavedScore(userData, time)

                if (currentScore > savedScore) {
                    when (time) {
                        30 -> userData.thirtySecondsScore = currentScore
                        60 -> userData.sixtySecondsScore = currentScore
                        90 -> userData.ninetySecondsScore = currentScore
                    }
                }
                when (time) {
                    30 -> {
                        userData.thirtyPlayed++
                        if (currentScore > 2.0) {
                            userData.coinValue += time
                        }
                    }

                    60 -> {
                        userData.sixtyPlayed++
                        if (currentScore > 6.0) {
                            userData.coinValue += time
                        }
                    }

                    90 -> {
                        userData.ninetyPlayed++
                        if (currentScore > 15.0) {
                            userData.coinValue += time
                        }
                    }
                }
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
                leaderboardRepository.updateLeaderboard(
                    currentScore,
                    getGameModeFromTime(time).gameCode
                )
            }
        }
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double {
        if (total == 0) return 0.0
        val accuracy = guessed.toDouble() / total
        return (guessed * accuracy.pow(WEIGHT_MODIFIER)).roundTo(2)
    }

    private fun getSavedScore(userData: UserDataEntity, time: Int): Double {
        return when (time) {
            30 -> userData.thirtySecondsScore
            60 -> userData.sixtySecondsScore
            90 -> userData.ninetySecondsScore
            else -> userData.thirtySecondsScore
        }
    }

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
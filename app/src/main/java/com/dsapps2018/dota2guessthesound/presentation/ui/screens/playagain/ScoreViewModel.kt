package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.getInitialUserData
import com.dsapps2018.dota2guessthesound.data.repository.ScoreRepository
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    companion object {
        const val WEIGHT_MODIFIER = 2.0
    }

    val userData: StateFlow<UserDataEntity> = scoreRepository.getUserDataFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getInitialUserData()
    )

    fun updateQuizScore(score: Int) {
        viewModelScope.launch {
            scoreRepository.getUserData()?.let { userData ->
                if (score > userData.quizScore) {
                    userData.quizScore = score
                }
                userData.quizPlayed++
                userData.isFresh = false
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
            }
        }
    }

    fun updateInvokerScore(score: Int) {
        viewModelScope.launch {
            scoreRepository.getUserData()?.let { userData ->
                if (score > userData.invokerScore) {
                    userData.invokerScore = score
                }
                userData.invokerPlayed++
                userData.isFresh = false
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
            }
        }
    }

    fun updateFastFingerScore(guessed: Int, total: Int, time: Int) {
        viewModelScope.launch {
            scoreRepository.getUserData()?.let { userData ->
                val currentScore = calculateFastFingerScore(guessed, total)
                val savedScore = getSavedScore(userData, time)

                if (currentScore > savedScore) {
                    when(time){
                        30 -> userData.thirtySecondsScore = currentScore
                        60 -> userData.sixtySecondsScore = currentScore
                        90 -> userData.ninetySecondsScore  = currentScore
                    }
                }
                when(time){
                    30 -> userData.thirtyPlayed++
                    60 -> userData.sixtyPlayed++
                    90 -> userData.ninetyPlayed++
                }
                userData.isFresh = false
                userData.modifiedAt = getCurrentDate()
                scoreRepository.updateUserData(userData)
            }
        }
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double {
        if (total == 0) return 0.0
        val accuracy = guessed.toDouble() / total
        return guessed * accuracy.pow(WEIGHT_MODIFIER)
    }

    private fun getSavedScore(userData: UserDataEntity, time: Int): Double{
        return when(time){
            30 -> userData.thirtySecondsScore
            60 -> userData.sixtySecondsScore
            90 -> userData.ninetySecondsScore
            else -> userData.thirtySecondsScore
        }
    }
}
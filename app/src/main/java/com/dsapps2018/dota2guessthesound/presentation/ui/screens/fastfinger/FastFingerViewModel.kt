package com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.data.analytics.AnalyticsMode
import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceSoundRound
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceWrongPolicy
import com.dsapps2018.dota2guessthesound.presentation.navigation.FastFingerModeDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastFingerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val round: MultipleChoiceSoundRound,
    private val productAnalytics: ProductAnalytics,
) : ViewModel() {

    private val timeSeconds = savedStateHandle.toRoute<FastFingerModeDestination>().time

    val quizEvent = round.events

    private val _triggerCorrectAnimation = MutableStateFlow(false)
    val triggerCorrectAnimation: StateFlow<Boolean> get() = _triggerCorrectAnimation

    private val _triggerWrongAnimation = MutableStateFlow(false)
    val triggerWrongAnimation: StateFlow<Boolean> get() = _triggerWrongAnimation

    init {
        viewModelScope.launch {
            round.start(MultipleChoiceWrongPolicy.AdvanceOnWrong)
            productAnalytics.modeStart(AnalyticsMode.fastFinger(timeSeconds))
        }
    }

    fun playSound() = round.playCurrent()

    fun onAnswerClicked(answer: String) {
        viewModelScope.launch {
            round.submitAnswer(answer)
        }
    }

    fun triggerCorrectImageAnimation() {
        _triggerCorrectAnimation.value = true
    }

    fun resetCorrectAnimationTrigger() {
        _triggerCorrectAnimation.value = false
    }

    fun triggerWrongImageAnimation() {
        _triggerWrongAnimation.value = true
    }

    fun resetWrongAnimationTrigger() {
        _triggerWrongAnimation.value = false
    }

    override fun onCleared() {
        super.onCleared()
        round.clear()
    }
}

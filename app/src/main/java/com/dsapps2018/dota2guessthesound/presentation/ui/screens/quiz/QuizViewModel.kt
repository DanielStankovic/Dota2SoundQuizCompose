package com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.quiz.ExtraLifeGate
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceSoundRound
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceWrongPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val round: MultipleChoiceSoundRound,
) : ViewModel() {

    val extraLifeGate = ExtraLifeGate()

    val quizEvent = round.events

    private val _triggerAnimation = MutableStateFlow(false)
    val triggerAnimation: StateFlow<Boolean> get() = _triggerAnimation

    init {
        viewModelScope.launch {
            round.start(MultipleChoiceWrongPolicy.StayOnWrong)
        }
    }

    fun playSound() = round.playCurrent()

    fun onAnswerClicked(answer: String) {
        viewModelScope.launch {
            round.submitAnswer(answer)
        }
    }

    fun triggerImageAnimation() {
        _triggerAnimation.value = true
    }

    fun resetAnimationTrigger() {
        _triggerAnimation.value = false
    }

    fun generateAndPlaySound() {
        viewModelScope.launch {
            round.continueAfterExtraLife()
        }
    }

    override fun onCleared() {
        super.onCleared()
        round.clear()
    }
}

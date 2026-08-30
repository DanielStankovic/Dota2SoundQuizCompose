package com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceSoundRound
import com.dsapps2018.dota2guessthesound.data.quiz.MultipleChoiceWrongPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastFingerViewModel @Inject constructor(
    private val round: MultipleChoiceSoundRound,
) : ViewModel() {

    val quizEvent = round.events

    private val _triggerCorrectAnimation = MutableStateFlow(false)
    val triggerCorrectAnimation: StateFlow<Boolean> get() = _triggerCorrectAnimation

    private val _triggerWrongAnimation = MutableStateFlow(false)
    val triggerWrongAnimation: StateFlow<Boolean> get() = _triggerWrongAnimation

    init {
        viewModelScope.launch {
            round.start(MultipleChoiceWrongPolicy.AdvanceOnWrong)
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

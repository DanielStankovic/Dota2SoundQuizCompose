package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.invoker.InvokerRound
import com.dsapps2018.dota2guessthesound.data.invoker.OrbType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvokerViewModel @Inject constructor(
    private val round: InvokerRound,
) : ViewModel() {

    val orbList = round.orbList
    val quizEvent = round.events
    val numOfHearts = round.numOfHearts
    val canInvoke = round.canInvoke
    val gameTimer = round.gameTimer
    val speedLevel = round.speedLevel
    val soundTimer = round.soundTimer
    val maxProgress = round.maxProgress

    init {
        viewModelScope.launch {
            round.start(viewModelScope)
        }
    }

    fun addElement(element: OrbType) = round.addOrb(element)

    fun checkAnswer() = round.invoke()

    fun playSound() = round.playCurrent()

    override fun onCleared() {
        super.onCleared()
        round.clear()
    }
}

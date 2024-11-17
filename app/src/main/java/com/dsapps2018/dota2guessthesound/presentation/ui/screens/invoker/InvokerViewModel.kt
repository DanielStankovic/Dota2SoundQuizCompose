package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.repository.InvokerRepository
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.dsapps2018.dota2guessthesound.data.util.connectivity.ConnectivityObserver
import com.dsapps2018.dota2guessthesound.data.util.connectivity.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class InvokerViewModel @Inject constructor(
    private val invokerRepository: InvokerRepository,
    private val soundPlayer: SoundPlayer,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    companion object {
        private const val LIST_MAX_SIZE = 3
        private const val GUESSED_SOUNDS_MODIFIER = 4
        private const val MAX_SPEED_LEVEL = 6
        private val speedTimeMap = mapOf<Int, Int>(
            1 to 9,
            2 to 8,
            3 to 7,
            4 to 6,
            5 to 5,
            6 to 4,
        )
    }

    private val fullList = mutableListOf<SoundModel>()
    private var currentSound: SoundModel? = null


    private val _orbList = MutableStateFlow<LinkedList<OrbType>>(LinkedList<OrbType>())
    val orbList = _orbList.asStateFlow()

    private val _quizEvent: MutableSharedFlow<InvokerEventState> =
        MutableSharedFlow<InvokerEventState>()
    val quizEvent = _quizEvent.asSharedFlow()

    private val _numOfHearts = MutableStateFlow(3)
    val numOfHearts = _numOfHearts.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(true)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _speedLevel = MutableStateFlow(1)
    val speedLevel = _speedLevel.asStateFlow()

    private val _soundTimer = MutableStateFlow(speedTimeMap[_speedLevel.value]!!)
    val soundTimer = _soundTimer.asStateFlow()

    private val _maxProgress = MutableStateFlow(speedTimeMap[_speedLevel.value]!!)
    val maxProgress = _maxProgress.asStateFlow()

    private var timerJob: Job? = null

    private var guessedSounds = 0

    init {
        viewModelScope.launch {
            invokerRepository.getInvokerSounds().collect { list ->
                fullList.addAll(list)
                delay(2000L)
                resetTimer()
                playNextSound()
            }
        }
    }

    private suspend fun onCorrectGuess() {
        resetTimer()
        playNextSound()
        guessedSounds++
        if (guessedSounds % GUESSED_SOUNDS_MODIFIER == 0 && _speedLevel.value < MAX_SPEED_LEVEL) {
            _speedLevel.value++
            _maxProgress.value = speedTimeMap[_speedLevel.value]!!
        }
    }

    private suspend fun onWrongGuess() {
        decreaseLives()
        resetTimer()
    }

    private suspend fun decreaseLives() {
        _numOfHearts.value--
        if (_numOfHearts.value <= 0) {
            //Game Over here
            _isTimerRunning.value = false
            _quizEvent.emit(InvokerEventState.GameOver)
            stopTimer()
        }
    }

    private fun startTimer() {
        stopTimer() // Ensure any previous timer is stopped
        timerJob = viewModelScope.launch {
            _soundTimer.value = speedTimeMap[speedLevel.value]!!
            while (_soundTimer.value > 0) {
                delay(1000L)
                _soundTimer.value--
                if (_soundTimer.value == 0) {
                    decreaseLives()
                    resetTimer() // Reset after timeout
                }
            }
        }
    }

    private fun resetTimer() {
        stopTimer()
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun playNextSound() {
        if (networkConnectivityObserver.isConnected() != ConnectivityObserver.Status.Available) {
            _quizEvent.emit(InvokerEventState.ConnectionLost)
        }

        currentSound = getNextSound(currentSound)

        currentSound?.let {
            soundPlayer.playSound(it.soundFileLink)

        }
    }

    private fun getNextSound(currentSound: SoundModel?): SoundModel {
        var newSound: SoundModel
        do {
            newSound = fullList.random()
        } while (newSound.id == currentSound?.id)
        return newSound
    }


    fun addElement(element: OrbType) {
        val updatedQueue = LinkedList(_orbList.value)

        if (updatedQueue.size == LIST_MAX_SIZE) {
            updatedQueue.removeLast() // Remove the oldest element
        }
        updatedQueue.addFirst(element) // Add the newest element to the front
        _orbList.value = updatedQueue
    }

    fun checkAnswer() {
        viewModelScope.launch {
            soundPlayer.stop()

            val spellOrbWrapper = getNumberOfOrbsForSpell(currentSound?.spellName ?: "")
            val userOrbWrapper = getNumberOfOrbsFromList(_orbList.value)

            if (spellOrbWrapper == userOrbWrapper) {
                onCorrectGuess()
            } else {
                onWrongGuess()
            }
        }
    }


    private fun getNumberOfOrbsForSpell(spellName: String): OrbWrapper {
        return when (spellName) {
            "Cold Snap" -> OrbWrapper(quasNum = 3, wexNum = 0, exortNum = 0)
            "Ghost Walk" -> OrbWrapper(quasNum = 2, wexNum = 1, exortNum = 0)
            "Ice Wall" -> OrbWrapper(quasNum = 2, wexNum = 0, exortNum = 1)
            "E.M.P" -> OrbWrapper(quasNum = 0, wexNum = 3, exortNum = 0)
            "Tornado" -> OrbWrapper(quasNum = 1, wexNum = 2, exortNum = 0)
            "Alacrity" -> OrbWrapper(quasNum = 0, wexNum = 2, exortNum = 1)
            "Sun Strike" -> OrbWrapper(quasNum = 0, wexNum = 0, exortNum = 3)
            "Forge Spirit" -> OrbWrapper(quasNum = 1, wexNum = 0, exortNum = 2)
            "Chaos Meteor" -> OrbWrapper(quasNum = 0, wexNum = 1, exortNum = 2)
            "Deafening Blast" -> OrbWrapper(quasNum = 1, wexNum = 1, exortNum = 1)
            else -> OrbWrapper(quasNum = 3, wexNum = 0, exortNum = 0)
        }
    }

    private fun getNumberOfOrbsFromList(orbList: LinkedList<OrbType>): OrbWrapper {
        return OrbWrapper(
            quasNum = orbList.count { it == OrbType.QUAS },
            wexNum = orbList.count { it == OrbType.WEX },
            exortNum = orbList.count { it == OrbType.EXORT })
    }

    fun playSound(){
        soundPlayer.playSound(currentSound?.soundFileLink!!)
    }
}

data class OrbWrapper(val quasNum: Int, val wexNum: Int, val exortNum: Int)
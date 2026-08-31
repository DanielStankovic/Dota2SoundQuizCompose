package com.dsapps2018.dota2guessthesound.data.invoker

import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayback
import com.dsapps2018.dota2guessthesound.data.util.connectivity.ConnectivityObserver
import com.dsapps2018.dota2guessthesound.data.util.connectivity.NetworkConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.LinkedList
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * Deep Data-layer module for one Invoker mode run.
 * See docs/adr/0008-invoker-round.md.
 */
class InvokerRound @Inject constructor(
    private val soundDao: SoundDao,
    private val soundPlayback: SoundPlayback,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
) {

    private val fullList = mutableListOf<SoundModel>()
    private var currentSound: SoundModel? = null
    private var guessedSounds = 0
    private var roundScope: CoroutineScope? = null
    private var soundTimerJob: Job? = null
    private var gameTimerJob: Job? = null

    private val _orbList = MutableStateFlow(LinkedList<OrbType>())
    val orbList: StateFlow<LinkedList<OrbType>> = _orbList.asStateFlow()

    private val _events = MutableSharedFlow<InvokerRoundEvent>()
    val events: SharedFlow<InvokerRoundEvent> = _events.asSharedFlow()

    private val _numOfHearts = MutableStateFlow(STARTING_HEARTS)
    val numOfHearts: StateFlow<Int> = _numOfHearts.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(true)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _canInvoke = MutableStateFlow(false)
    val canInvoke: StateFlow<Boolean> = _canInvoke.asStateFlow()

    private val _gameTimer = MutableStateFlow(0)
    val gameTimer: StateFlow<Int> = _gameTimer.asStateFlow()

    private val _speedLevel = MutableStateFlow(1)
    val speedLevel: StateFlow<Int> = _speedLevel.asStateFlow()

    private val _soundTimer = MutableStateFlow(speedTimeMap[1]!!)
    val soundTimer: StateFlow<Int> = _soundTimer.asStateFlow()

    private val _maxProgress = MutableStateFlow(speedTimeMap[1]!!)
    val maxProgress: StateFlow<Int> = _maxProgress.asStateFlow()

    suspend fun start(scope: CoroutineScope) {
        roundScope = scope
        fullList.clear()
        currentSound = null
        guessedSounds = 0
        _orbList.value = LinkedList()
        _numOfHearts.value = STARTING_HEARTS
        _isTimerRunning.value = true
        _canInvoke.value = false
        _gameTimer.value = 0
        _speedLevel.value = 1
        _maxProgress.value = speedTimeMap[1]!!
        _soundTimer.value = speedTimeMap[1]!!
        stopSoundTimer()

        gameTimerJob?.cancel()
        gameTimerJob = scope.launch {
            while (_isTimerRunning.value) {
                delay(1.seconds)
                _gameTimer.value++
            }
        }

        val sounds = soundDao.getInvokerSounds().map { it.shuffled() }.first()
        fullList.addAll(sounds)
        delay(START_DELAY_MS)
        resetSoundTimer()
        playNextSound()
        _canInvoke.value = true
    }

    fun addOrb(orb: OrbType) {
        val updated = LinkedList(_orbList.value)
        if (updated.size == LIST_MAX_SIZE) {
            updated.removeLast()
        }
        updated.addFirst(orb)
        _orbList.value = updated
    }

    fun invoke() {
        val scope = roundScope ?: return
        scope.launch {
            soundPlayback.stop()
            val expected = recipeForSpell(currentSound?.spellName.orEmpty())
            val actual = countsFromOrbs(_orbList.value)
            if (expected == actual) {
                onCorrectGuess()
            } else {
                onWrongGuess()
            }
        }
    }

    fun playCurrent() {
        currentSound?.let { soundPlayback.play(it) }
    }

    fun clear() {
        soundPlayback.stop()
        stopSoundTimer()
        gameTimerJob?.cancel()
        gameTimerJob = null
        _isTimerRunning.value = false
        roundScope = null
    }

    private suspend fun onCorrectGuess() {
        resetSoundTimer()
        playNextSound()
        guessedSounds++
        if (guessedSounds % GUESSED_SOUNDS_MODIFIER == 0 && _speedLevel.value < MAX_SPEED_LEVEL) {
            _speedLevel.value++
            _maxProgress.value = speedTimeMap[_speedLevel.value]!!
        }
    }

    private suspend fun onWrongGuess() {
        decreaseLives()
    }

    private suspend fun decreaseLives() {
        _numOfHearts.value--
        if (_numOfHearts.value <= 0) {
            _isTimerRunning.value = false
            _events.emit(InvokerRoundEvent.GameOver(_gameTimer.value))
            stopSoundTimer()
        } else {
            resetSoundTimer()
        }
    }

    private fun startSoundTimer() {
        val scope = roundScope ?: return
        soundTimerJob = scope.launch {
            _soundTimer.value = speedTimeMap[_speedLevel.value]!!
            while (_soundTimer.value > 0) {
                delay(1.seconds)
                _soundTimer.value--
                if (_soundTimer.value == 0) {
                    decreaseLives()
                }
            }
        }
    }

    private fun resetSoundTimer() {
        stopSoundTimer()
        startSoundTimer()
    }

    private fun stopSoundTimer() {
        soundTimerJob?.cancel()
        soundTimerJob = null
    }

    private suspend fun playNextSound() {
        if (networkConnectivityObserver.isConnected() != ConnectivityObserver.Status.Available) {
            _events.emit(InvokerRoundEvent.ConnectionLost)
        }
        currentSound = nextSound(currentSound)
        currentSound?.let { soundPlayback.play(it) }
    }

    private fun nextSound(current: SoundModel?): SoundModel {
        var candidate: SoundModel
        do {
            candidate = fullList.random()
        } while (candidate.id == current?.id)
        return candidate
    }

    private fun recipeForSpell(spellName: String): OrbCounts = when (spellName) {
        "Cold Snap" -> OrbCounts(quas = 3, wex = 0, exort = 0)
        "Ghost Walk" -> OrbCounts(quas = 2, wex = 1, exort = 0)
        "Ice Wall" -> OrbCounts(quas = 2, wex = 0, exort = 1)
        "E.M.P" -> OrbCounts(quas = 0, wex = 3, exort = 0)
        "Tornado" -> OrbCounts(quas = 1, wex = 2, exort = 0)
        "Alacrity" -> OrbCounts(quas = 0, wex = 2, exort = 1)
        "Sun Strike" -> OrbCounts(quas = 0, wex = 0, exort = 3)
        "Forge Spirit" -> OrbCounts(quas = 1, wex = 0, exort = 2)
        "Chaos Meteor" -> OrbCounts(quas = 0, wex = 1, exort = 2)
        "Deafening Blast" -> OrbCounts(quas = 1, wex = 1, exort = 1)
        else -> OrbCounts(quas = 3, wex = 0, exort = 0)
    }

    private fun countsFromOrbs(orbs: LinkedList<OrbType>): OrbCounts =
        OrbCounts(
            quas = orbs.count { it == OrbType.QUAS },
            wex = orbs.count { it == OrbType.WEX },
            exort = orbs.count { it == OrbType.EXORT },
        )

    private data class OrbCounts(val quas: Int, val wex: Int, val exort: Int)

    companion object {
        private const val LIST_MAX_SIZE = 3
        private const val GUESSED_SOUNDS_MODIFIER = 4
        private const val MAX_SPEED_LEVEL = 6
        private const val STARTING_HEARTS = 3
        private const val START_DELAY_MS = 2000L
        private val speedTimeMap = mapOf(
            1 to 9,
            2 to 8,
            3 to 7,
            4 to 6,
            5 to 5,
            6 to 4,
        )
    }
}

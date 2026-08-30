package com.dsapps2018.dota2guessthesound.data.quiz

import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayback
import com.dsapps2018.dota2guessthesound.data.util.connectivity.ConnectivityObserver
import com.dsapps2018.dota2guessthesound.data.util.connectivity.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Deep in-process module for a multiple-choice spell-sound round (Quiz / Fast Finger).
 * See docs/adr/0006-multiple-choice-sound-round.md.
 */
class MultipleChoiceSoundRound @Inject constructor(
    private val soundDao: SoundDao,
    private val soundPlayback: SoundPlayback,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
) {

    private val fullList = mutableListOf<SoundModel>()
    private val playedSounds = mutableListOf<SoundModel>()
    private val remainingSounds = mutableListOf<SoundModel>()
    private var currentSound: SoundModel? = null
    private var policy: MultipleChoiceWrongPolicy = MultipleChoiceWrongPolicy.StayOnWrong

    private val _events = MutableSharedFlow<MultipleChoiceEvent>()
    val events: SharedFlow<MultipleChoiceEvent> = _events.asSharedFlow()

    suspend fun start(policy: MultipleChoiceWrongPolicy) {
        this.policy = policy
        fullList.clear()
        playedSounds.clear()
        remainingSounds.clear()
        currentSound = null

        val sounds = soundDao.getAllSounds().map { it.shuffled() }.first()
        fullList.addAll(sounds)
        remainingSounds.addAll(sounds)
        playNextSound()
    }

    fun playCurrent() {
        currentSound?.let { soundPlayback.play(it) }
    }

    suspend fun submitAnswer(answer: String) {
        soundPlayback.stop()
        if (answer == currentSound?.spellName) {
            _events.emit(MultipleChoiceEvent.Correct)
            playNextSound()
        } else {
            _events.emit(MultipleChoiceEvent.Wrong)
            if (policy == MultipleChoiceWrongPolicy.AdvanceOnWrong) {
                playNextSound()
            }
        }
    }

    /** After Quiz rewarded continue: deal the next sound without resetting the pool. */
    suspend fun continueAfterExtraLife() {
        playNextSound()
    }

    fun clear() {
        soundPlayback.stop()
    }

    private suspend fun playNextSound() {
        if (networkConnectivityObserver.isConnected() != ConnectivityObserver.Status.Available) {
            _events.emit(MultipleChoiceEvent.ConnectionLost)
        }
        currentSound = takeNextSound()
        if (currentSound == null) {
            _events.emit(MultipleChoiceEvent.NoMoreSounds)
            return
        }
        currentSound?.let {
            _events.emit(MultipleChoiceEvent.SoundReady(getButtonOptions(it)))
            soundPlayback.play(it)
        }
    }

    private fun takeNextSound(): SoundModel? {
        if (remainingSounds.isEmpty()) return null
        val nextSound = remainingSounds.random()
        playedSounds.add(nextSound)
        remainingSounds.remove(nextSound)
        return nextSound
    }

    private fun getButtonOptions(correctSound: SoundModel, numberOfOptions: Int = 4): List<String> {
        val uniqueOptions = mutableSetOf(correctSound.spellName)
        while (uniqueOptions.size < numberOfOptions) {
            uniqueOptions.add(fullList.random().spellName)
        }
        return uniqueOptions.shuffled()
    }
}

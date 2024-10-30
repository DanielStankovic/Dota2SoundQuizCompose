package com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.repository.QuizRepository
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private var soundPlayer: SoundPlayer,
) : ViewModel() {

    private val fullList = mutableListOf<SoundModel>()
    private val playedSounds = mutableListOf<SoundModel>()
    private val remainingSounds = mutableListOf<SoundModel>()
    private var currentSound: SoundModel? = null

    private val _quizEvent: MutableSharedFlow<QuizEventState> =
        MutableSharedFlow<QuizEventState>()
    val quizEvent = _quizEvent.asSharedFlow()

    private val _triggerAnimation = MutableStateFlow(false)
    val triggerAnimation: StateFlow<Boolean> get() = _triggerAnimation

    init {
        viewModelScope.launch {
            quizRepository.getAllSounds().collect { list ->
                fullList.addAll(list)
                remainingSounds.addAll(list)
                playNextSound()
            }

        }
    }

    private suspend fun playNextSound(){
        currentSound = getNextSound()
        if(currentSound == null){
            _quizEvent.emit(QuizEventState.NoMoreSounds)
            return
        }
        currentSound?.let {
            _quizEvent.emit(QuizEventState.SoundReady(getButtonOptions(it)))
            soundPlayer.playSound(it.soundFileLink)
        }
    }
    private fun getNextSound(): SoundModel? {
        if (remainingSounds.isEmpty()) return null
        val nextSound = remainingSounds.random()
        playedSounds.add(nextSound)
        remainingSounds.remove(nextSound)
        return nextSound
    }

    private fun getButtonOptions(correctSound: SoundModel, numberOfOptions: Int = 4): List<String> {
        val uniqueOptions = mutableSetOf(correctSound.spellName)

        // Randomly add other sound names that have not been selected already
        while (uniqueOptions.size < numberOfOptions) {
            val randomSound = fullList.random()  // Sound names can repeat on buttons
            uniqueOptions.add(randomSound.spellName)
        }

        return uniqueOptions.shuffled() // Shuffle to randomize button order
    }

    fun playSound(){
       soundPlayer.playSound(currentSound?.soundFileLink!!)
//        if(!mediaPlayer.isPlaying){
//            mediaPlayer.start()
//        }
    }

    fun onAnswerClicked(answer: String){
        viewModelScope.launch{
           soundPlayer.stop()

            if(answer == currentSound?.spellName){
                _quizEvent.emit(QuizEventState.CorrectSound)
                playNextSound()
            }else{
                _quizEvent.emit(QuizEventState.WrongSound)
            }
        }
    }

    // Method to trigger animation
    fun triggerImageAnimation() {
        _triggerAnimation.value = true
    }

    // Reset trigger after the animation completes
    fun resetAnimationTrigger() {
        _triggerAnimation.value = false
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.stop()
    }

}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.repository.QuizRepository
import com.dsapps2018.dota2guessthesound.data.util.SoundFileMapper
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.dsapps2018.dota2guessthesound.data.util.connectivity.ConnectivityObserver
import com.dsapps2018.dota2guessthesound.data.util.connectivity.NetworkConnectivityObserver
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz.QuizEventState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FastFingerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val quizRepository: QuizRepository,
    private val soundPlayer: SoundPlayer,
    private val networkConnectivityObserver: NetworkConnectivityObserver
): ViewModel() {


    private val fullList = mutableListOf<SoundModel>()
    private val playedSounds = mutableListOf<SoundModel>()
    private val remainingSounds = mutableListOf<SoundModel>()
    private var currentSound: SoundModel? = null

    private val _quizEvent: MutableSharedFlow<QuizEventState> =
        MutableSharedFlow<QuizEventState>()
    val quizEvent = _quizEvent.asSharedFlow()

    private val _triggerCorrectAnimation = MutableStateFlow(false)
    val triggerCorrectAnimation: StateFlow<Boolean> get() = _triggerCorrectAnimation

    private val _triggerWrongAnimation = MutableStateFlow(false)
    val triggerWrongAnimation: StateFlow<Boolean> get() = _triggerWrongAnimation

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
        if(networkConnectivityObserver.isConnected() != ConnectivityObserver.Status.Available){
            _quizEvent.emit(QuizEventState.ConnectionLost)
        }
        currentSound = getNextSound()
        if(currentSound == null){
            _quizEvent.emit(QuizEventState.NoMoreSounds)
            return
        }
        currentSound?.let {
            _quizEvent.emit(QuizEventState.SoundReady(getButtonOptions(it)))

//            soundPlayer.playSound(it.soundFileLink)

            playSoundFromSoundModel(it)
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

    private fun playSoundFromSoundModel(currentSound: SoundModel?){
        currentSound?.let {
            if(it.isLocal){
                val resourceId = SoundFileMapper.map[it.spellName]
                if(resourceId == null){
                    return
                }
                val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
                soundPlayer.playSoundFromResource(uri)
            }else{
                if(it.soundFileLink.isNotEmpty()) {
                    soundPlayer.playSound(it.soundFileLink)
                }
            }
        }
    }

    fun playSound(){
        playSoundFromSoundModel(currentSound)
//        soundPlayer.playSound(currentSound?.soundFileLink!!)
    }

    fun onAnswerClicked(answer: String){
        viewModelScope.launch{
            soundPlayer.stop()

            if(answer == currentSound?.spellName){
                _quizEvent.emit(QuizEventState.CorrectSound)
                playNextSound()
            }else{
                _quizEvent.emit(QuizEventState.WrongSound)
                playNextSound()
            }
        }
    }

    // Method to trigger correct animation
    fun triggerCorrectImageAnimation() {
        _triggerCorrectAnimation.value = true
    }

    // Reset trigger after the correct animation completes
    fun resetCorrectAnimationTrigger() {
        _triggerCorrectAnimation.value = false
    }

    // Method to trigger wrong animation
    fun triggerWrongImageAnimation() {
        _triggerWrongAnimation.value = true
    }

    // Reset trigger after the wrong animation completes
    fun resetWrongAnimationTrigger() {
        _triggerWrongAnimation.value = false
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.stop()
    }
}
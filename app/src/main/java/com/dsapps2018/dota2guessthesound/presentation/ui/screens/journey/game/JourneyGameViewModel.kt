package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.repository.JourneyRepository
import com.dsapps2018.dota2guessthesound.data.util.SoundFileMapper
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.dsapps2018.dota2guessthesound.presentation.navigation.JourneyGameDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyGameViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: Resources,
    savedStateHandle: SavedStateHandle,
    private val soundPlayer: SoundPlayer,
    private val journeyRepository: JourneyRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    var additionalLifeUsed: Boolean = false

    private val destination = savedStateHandle.toRoute<JourneyGameDestination>()

    val levelNum: StateFlow<Int> = MutableStateFlow(destination.levelNum)

    private val _numOfHearts = MutableStateFlow(2)
    val numOfHearts = _numOfHearts.asStateFlow()

    private val _journeyDataState =
        MutableStateFlow<JourneyGameFetchState>(JourneyGameFetchState.Loading)
    val journeyDataState = _journeyDataState.asStateFlow()

    private val _selectedSoundStates = mutableStateMapOf<Int, Boolean>()
    val selectedSoundState: Map<Int, Boolean> get() = _selectedSoundStates

    private val _gameEvent: MutableSharedFlow<JourneyGameEvent> =
        MutableSharedFlow()
    val gameEvent = _gameEvent.asSharedFlow()

    private val _showWatchAdContinueDialog = MutableStateFlow(false)
    val showWatchAdContinueDialog = _showWatchAdContinueDialog.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _journeyDataState.value =
                JourneyGameFetchState.Error(context.getString(R.string.level_fetch_error))
        }

    init {
        fetchLevelData()
    }

    private fun fetchLevelData() = viewModelScope.launch(coroutineExceptionHandler) {

        _journeyDataState.value = JourneyGameFetchState.Loading

        val levelData = journeyRepository.getLevelData(levelNum.value)
        val heroIds = levelData.radiantHeroes + levelData.direHeroes
        val journeySounds = journeyRepository.getJourneySounds(heroIds)
        val (correctSounds, incorrectSounds) = journeySounds.partition { it.isCorrectSound }
        val randomSounds = incorrectSounds.shuffled().take(levelData.maxSounds - correctSounds.size)
        val soundList = (correctSounds + randomSounds).shuffled()
        val radiantHeroImages =
            journeyRepository.getCasterNames(levelData.radiantHeroes).map { name ->
                resources.getIdentifier(
                    "hero_${name.lowercase().replace("'s", "s").replace("-", "")}",
                    "drawable",
                    context.packageName
                )
            }
        val direHeroImages = journeyRepository.getCasterNames(levelData.direHeroes).map { name ->
            resources.getIdentifier(
                name.replace("'s", "s").replace("-", ""), "drawable", context.packageName
            )
        }

        _selectedSoundStates.putAll(soundList.map { x -> x.soundModel.id to false })
        _journeyDataState.value = JourneyGameFetchState.Success(
            JourneyGameModel(
                levelNum.value, radiantHeroImages, direHeroImages, soundList, correctSounds.size
            )
        )
    }

    fun toggleSoundCardState(soundId: Int) {
        _selectedSoundStates[soundId]?.let { _selectedSoundStates[soundId] = !it }
    }

    fun playSound(currentSound: SoundModel?) {
        currentSound?.let {
            if (it.isLocal) {
                val resourceId = SoundFileMapper.map[it.spellName]
                if (resourceId == null) {
                    return
                }
                val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
                soundPlayer.playSoundFromResource(uri)
            } else {
                if (it.soundFileLink.isNotEmpty()) {
                    soundPlayer.playSound(it.soundFileLink)
                }
            }
        }
    }

    fun submitAnswer() = viewModelScope.launch {
        val correctSounds =
            (_journeyDataState.value as JourneyGameFetchState.Success).data.soundList.filter { x -> x.isCorrectSound }
                .map { x -> x.soundModel.id }

        val selectedSounds = _selectedSoundStates.filter { x -> x.value == true }.keys

//        if (selectedSounds.size > correctSounds.size) {
//            _gameEvent.emit(JourneyGameEvent.Wrong)
//
//        }
//        if (selectedSounds.size < correctSounds.size) {
//            _gameEvent.emit(JourneyGameEvent.Wrong)
//        }
//        if (correctSounds.containsAll(selectedSounds)) {
//            _gameEvent.emit(JourneyGameEvent.Correct)
//        } else {
//            _gameEvent.emit(JourneyGameEvent.Wrong)
//        }

        if (
            selectedSounds.size == correctSounds.size &&
            correctSounds.containsAll(selectedSounds)
        ) {
            _gameEvent.emit(JourneyGameEvent.Correct)
        } else {
            _numOfHearts.value--
            if (_numOfHearts.value <= 0) {
                if(!additionalLifeUsed){
                    _showWatchAdContinueDialog.value = true
                }else{
                    _gameEvent.emit(JourneyGameEvent.GameOver)
                }
            }

        }
    }

    fun setShowWatchAdContinueDialog(showDialog: Boolean) {
        _showWatchAdContinueDialog.value = showDialog
    }

    fun increaseNumOfHearts() {
        _numOfHearts.value++
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.stop()
    }

}
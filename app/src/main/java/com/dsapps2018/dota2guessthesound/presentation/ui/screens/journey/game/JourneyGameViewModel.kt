package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel
import com.dsapps2018.dota2guessthesound.data.repository.JourneyRepository
import com.dsapps2018.dota2guessthesound.presentation.navigation.JourneyGameDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyGameViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: Resources,
    savedStateHandle: SavedStateHandle,
    private val journeyRepository: JourneyRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    val levelNum: Int = savedStateHandle.toRoute<JourneyGameDestination>().levelNum

    private val _journeyDataState =
        MutableStateFlow<JourneyLevelFetchState>(JourneyLevelFetchState.Loading)
    val journeyDataState = _journeyDataState.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _journeyDataState.value =
                JourneyLevelFetchState.Error(context.getString(R.string.level_fetch_error))
        }

    init {
        fetchLevelData()
    }

    private fun fetchLevelData() = viewModelScope.launch(coroutineExceptionHandler) {
        _journeyDataState.value = JourneyLevelFetchState.Loading

        val levelData = journeyRepository.getLevelData(levelNum)
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
                name.replace("'s", "s").replace("-", ""),
                "drawable",
                context.packageName
            )
        }

        _journeyDataState.value = JourneyLevelFetchState.Success(
            JourneyGameModel(
                levelNum,
                radiantHeroImages,
                direHeroImages,
                soundList,
                correctSounds.size
            )
        )
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.repository.JourneyLevelRepository
import com.dsapps2018.dota2guessthesound.data.repository.ScoreRepository
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.JourneyGameFetchState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyLevelViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    scoreRepository: ScoreRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val journeyLevelRepository: JourneyLevelRepository
) : ViewModel() {

    private val _journeyLevelState =
        MutableStateFlow<JourneyLevelFetchState>(JourneyLevelFetchState.Loading)
    val journeyLevelState = _journeyLevelState.asStateFlow()

    val journeyLevel: StateFlow<Int> = scoreRepository.getUserJourneyLevelFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val journeyProgressText: StateFlow<String> = combine(
        journeyLevelState,
        journeyLevel
    ) { state, level ->
        when (state) {
            is JourneyLevelFetchState.Success -> {
                "Level $level of ${state.data.size}"
            }
            else -> ""
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _journeyLevelState.value =
                JourneyLevelFetchState.Error(context.getString(R.string.level_fetch_error))
        }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            _journeyLevelState.value = JourneyLevelFetchState.Loading
            val levelData = journeyLevelRepository.getLevelsData()
            _journeyLevelState.value = JourneyLevelFetchState.Success(levelData)
        }
    }
}
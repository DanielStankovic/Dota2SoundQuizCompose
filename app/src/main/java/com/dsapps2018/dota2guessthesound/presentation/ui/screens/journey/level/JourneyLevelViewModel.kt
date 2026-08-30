package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.model.JourneyLevelModel
import com.dsapps2018.dota2guessthesound.data.repository.JourneyLevelRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyLevelViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    playerProgressRepository: PlayerProgressRepository,
    private val resources: Resources,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val journeyLevelRepository: JourneyLevelRepository
) : ViewModel() {

    private val _journeyLevelState =
        MutableStateFlow<JourneyLevelFetchState>(JourneyLevelFetchState.Loading)
    val journeyLevelState = _journeyLevelState.asStateFlow()

    val journeyLevel: StateFlow<Int> = playerProgressRepository.journeyLevel().stateIn(
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

    private val _showAffixBottomSheet = MutableStateFlow(false)
    val showAffixBottomSheet = _showAffixBottomSheet.asStateFlow()

    private val _currentAffix: MutableStateFlow<AffixModel?> = MutableStateFlow(null)
    val currentAffix = _currentAffix.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _journeyLevelState.value =
                JourneyLevelFetchState.Error(context.getString(R.string.level_fetch_error))
        }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            _journeyLevelState.value = JourneyLevelFetchState.Loading
            val affixList = journeyLevelRepository.getAllAffixes()
            val levelData = journeyLevelRepository.getLevelsData().map { level ->
                JourneyLevelModel(
                    id = level.id,
                    level = level.level,
                    affixes = level.affixes.map { level ->
                       val affix = affixList.find { x -> x.id == level }!!
                        AffixModel(
                            id = affix.id,
                            affix = affix.affix,
                            description = affix.description,
                            iconResourceId = resources.getIdentifier(
                                "affix_${affix.affix.lowercase().replace(" ", "_")}",
                                "drawable",
                                context.packageName
                            ),
                            data = affix.data
                        )
                    }
                )
            }
            _journeyLevelState.value = JourneyLevelFetchState.Success(levelData)
        }
    }

    fun setShowAffixBottomSheet(show: Boolean){
        _showAffixBottomSheet.value = show
    }

    fun setCurrentAffix(affix: AffixModel?){
        _currentAffix.value = affix
    }
}
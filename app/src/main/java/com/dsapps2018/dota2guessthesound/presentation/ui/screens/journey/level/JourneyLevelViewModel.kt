package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.journey.JourneyLevelsState
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRound
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    playerProgressRepository: PlayerProgressRepository,
    private val journeyRound: JourneyRound,
) : ViewModel() {

    val journeyLevelState: StateFlow<JourneyLevelsState> = journeyRound.levelsState

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
            is JourneyLevelsState.Success -> {
                "Level $level of ${state.levels.size}"
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

    init {
        viewModelScope.launch {
            journeyRound.loadLevels()
        }
    }

    fun setShowAffixBottomSheet(show: Boolean) {
        _showAffixBottomSheet.value = show
    }

    fun setCurrentAffix(affix: AffixModel?) {
        _currentAffix.value = affix
    }
}

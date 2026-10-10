package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.auth.AuthSession
import com.dsapps2018.dota2guessthesound.data.journey.JourneyLevelsState
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRound
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.dsapps2018.dota2guessthesound.data.progress.LevelClearTime
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.status.SessionStatus
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
    private val journeyRound: JourneyRound,
    private val journeyIssue: JourneyIssue,
    private val authSession: AuthSession,
    levelClearTime: LevelClearTime,
) : ViewModel() {

    val journeyLevel: StateFlow<Int> = playerProgressRepository.journeyLevel().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val journeyLevelState: StateFlow<JourneyLevelsState> =
        journeyRound.presentedLevels(playerProgressRepository.journeyLevel()).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = JourneyLevelsState.Loading,
        )

    val personalBests: StateFlow<Map<Int, Long>> = levelClearTime.observePersonalBests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap(),
    )

    val journeyProgressText: StateFlow<String> = combine(
        journeyLevelState,
        journeyLevel
    ) { state, level ->
        when (state) {
            is JourneyLevelsState.Success -> {
                val total = state.levels.size
                val maxLevel = state.levels.maxOfOrNull { it.level } ?: 0
                if (maxLevel > 0 && level >= maxLevel) {
                    context.getString(R.string.journey_progress_complete)
                } else {
                    "Level $level of $total"
                }
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

    /**
     * Picker card to restore after leaving (game / result / library).
     * Null until the player opens a level, continues Journey, or the pager is disposed —
     * so first entry can still follow the playable frontier as progress loads.
     */
    private val _pickerLevel = MutableStateFlow<Int?>(null)
    val pickerLevel = _pickerLevel.asStateFlow()

    /** True when Active Issue or completed Issues exist (Library openable). */
    private val _libraryAvailable = MutableStateFlow(false)
    val libraryAvailable = _libraryAvailable.asStateFlow()

    init {
        viewModelScope.launch {
            journeyRound.loadLevels()
        }
        viewModelScope.launch {
            authSession.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _libraryAvailable.value = journeyIssue.hasLibraryEntries()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _libraryAvailable.value = false
                    }
                    // Initializing / RefreshFailure: keep last known value (resume race).
                    else -> Unit
                }
            }
        }
    }

    /**
     * Re-check Library chip after returning to this screen (e.g. first-clear grant).
     * Skips when Auth has no user yet so a mid-resume token refresh cannot disable the chip.
     */
    fun refreshLibraryAvailability() = viewModelScope.launch {
        when (authSession.sessionStatus.value) {
            is SessionStatus.Authenticated -> {
                _libraryAvailable.value = journeyIssue.hasLibraryEntries()
            }
            is SessionStatus.NotAuthenticated -> {
                _libraryAvailable.value = false
            }
            else -> Unit
        }
    }

    fun setShowAffixBottomSheet(show: Boolean) {
        _showAffixBottomSheet.value = show
    }

    fun setCurrentAffix(affix: AffixModel?) {
        _currentAffix.value = affix
    }

    fun setPickerLevel(level: Int) {
        _pickerLevel.value = level
    }
}

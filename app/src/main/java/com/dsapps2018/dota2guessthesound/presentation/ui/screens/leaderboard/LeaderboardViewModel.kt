package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _leaderboardState = MutableStateFlow<LeaderboardFetchState>(LeaderboardFetchState.Loading)
    val leaderboardState = _leaderboardState.asStateFlow()

    val leaderboardMonth: StateFlow<String> = leaderboardRepository.getLeaderboardMonth().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    init {
        viewModelScope.launch{
            leaderboardRepository.fetchLeaderboardStanding()
        }
    }

    sealed interface LeaderboardFetchState {
        data object Loading : LeaderboardFetchState
        data class Success(val data: Any) : LeaderboardFetchState
        data class Error(val error: String): LeaderboardFetchState
    }
}
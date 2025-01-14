package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard.leaderboardhistory

import com.dsapps2018.dota2guessthesound.data.model.LeaderboardHistoryModel

sealed interface HistoryFetchState {
    data object Loading : HistoryFetchState
    data class Success(val data: List<LeaderboardHistoryModel>) : HistoryFetchState
    data class Error(val error: String) : HistoryFetchState
}
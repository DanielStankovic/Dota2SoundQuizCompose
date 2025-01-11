package com.dsapps2018.dota2guessthesound.presentation.ui.screens.rewards

import com.dsapps2018.dota2guessthesound.data.api.response.RewardDto

sealed interface RewardsFetchState {
    data object Loading : RewardsFetchState
    data class Success(val data: List<RewardDto>) : RewardsFetchState
    data class Error(val error: String) : RewardsFetchState
}
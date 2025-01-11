package com.dsapps2018.dota2guessthesound.presentation.ui.screens.rewards

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.repository.RewardsRepository
import com.dsapps2018.dota2guessthesound.presentation.navigation.RewardsDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val rewardsRepository: RewardsRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics

) : ViewModel() {

    val leaderboardId: Int = savedStateHandle.toRoute<RewardsDestination>().leaderboardId

    private val _rewardsState =
        MutableStateFlow<RewardsFetchState>(RewardsFetchState.Loading)
    val rewardsState = _rewardsState.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _rewardsState.value =
                RewardsFetchState.Error(context.getString(R.string.rewards_fetch_error))
        }

    init {
        fetchRewardsData()
    }

    private fun fetchRewardsData() = viewModelScope.launch(coroutineExceptionHandler) {
        _rewardsState.value = RewardsFetchState.Loading

        val rewardsList = rewardsRepository.getRewardsData(1)
        val sortedList = if (rewardsList.isNotEmpty()) {
            rewardsList.sortedBy {
                when (it.standing) {
                    2 -> 0
                    1 -> 1
                    3 -> 2
                    else -> 3
                }
            }
        } else {
            rewardsList
        }

        _rewardsState.value = RewardsFetchState.Success(sortedList)
    }
}
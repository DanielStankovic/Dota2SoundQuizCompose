package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard.leaderboardhistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardHistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val leaderboardRepository: LeaderboardRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _historyState =
        MutableStateFlow<HistoryFetchState>(HistoryFetchState.Loading)
    val historyState = _historyState.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _historyState.value =
                HistoryFetchState.Error(context.getString(R.string.history_fetch_error))
        }

    init {
        fetchHistoryData()
    }

    private fun fetchHistoryData() = viewModelScope.launch(coroutineExceptionHandler) {
        _historyState.value = HistoryFetchState.Loading
        val historyList = leaderboardRepository.fetchLeaderboardHistory()
        _historyState.value = HistoryFetchState.Success(historyList)
    }
}
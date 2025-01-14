package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardDto
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.presentation.navigation.LeaderboardDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val leaderboardRepository: LeaderboardRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    val leaderboardId: Int? = savedStateHandle.toRoute<LeaderboardDestination>().leaderboardId

    private val _leaderboardState =
        MutableStateFlow<LeaderboardFetchState>(LeaderboardFetchState.Loading)
    val leaderboardState = _leaderboardState.asStateFlow()

    private val _countdownFlow = MutableStateFlow("")
    val countdownFlow: StateFlow<String> = _countdownFlow.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            firebaseCrashlytics.recordException(throwable)
            _leaderboardState.value =
                LeaderboardFetchState.Error(context.getString(R.string.leaderboard_standing_fetch_error))
        }

    fun fetchLeaderboardStanding() = viewModelScope.launch(coroutineExceptionHandler) {
        val top10StandingResult =
            async(Dispatchers.IO) { leaderboardRepository.fetchTop10LeaderboardStandings(leaderboardId) }.await()
        val currentUserStandingResult =
            async(Dispatchers.IO) { leaderboardRepository.fetchCurrentUserLeaderboardStandings(leaderboardId) }.await()

        val leaderboardData =
            async(Dispatchers.IO) { leaderboardRepository.fetchLeaderboardData(leaderboardId) }.await()

        startCountdown(leaderboardData.endAt, leaderboardData.serverTimestamp)

        val completeResult =
            if (top10StandingResult.any { x -> x.isCurrentUser }) top10StandingResult else top10StandingResult + currentUserStandingResult



        _leaderboardState.value = LeaderboardFetchState.Success(completeResult, leaderboardData)
    }

    private fun startCountdown(endDate: String, initialServerTimestamp: String) =
        viewModelScope.launch(coroutineExceptionHandler) {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val targetDateTime = ZonedDateTime.parse(endDate, formatter)

            val initialServerTime = ZonedDateTime.parse(initialServerTimestamp, formatter)
            val localTimeAtFetch = ZonedDateTime.now()
            val serverTimeOffset = ChronoUnit.MILLIS.between(localTimeAtFetch, initialServerTime)

            while (true) {
                val localNow = ZonedDateTime.now()
                val correctedNow = localNow.plus(serverTimeOffset, ChronoUnit.MILLIS)

                val remainingTime = ChronoUnit.MILLIS.between(correctedNow, targetDateTime)

                if (remainingTime <= 0) {
                    _countdownFlow.value = "Congratulations\nWinners!"
                    break
                } else {

                    // Calculate remaining time
                    val days = remainingTime / (24 * 60 * 60 * 1000)
                    val hours = (remainingTime / (60 * 60 * 1000)) % 24
                    val minutes = (remainingTime / (60 * 1000)) % 60
                    val seconds = (remainingTime / 1000) % 60

                    val daysString = if (days <= 1) "day" else "days"
                    // Update only if the visible value changes
                    val newCountdownText = if (days > 0) String.format(
                        Locale.getDefault(),
                        "%02d $daysString\n%02dh:%02dm:%02ds",
                        days, hours, minutes, seconds
                    ) else String.format(
                        Locale.getDefault(),
                        "%02dh:%02dm:%02ds",
                        hours, minutes, seconds
                    )
                    if (_countdownFlow.value != newCountdownText) {
                        _countdownFlow.value = "Ends in\n$newCountdownText"
                    }

                    // Delay until the next second boundary
                    val delayMillis = 1000 - (localNow.nano / 1_000_000)
                    delay(delayMillis.toLong())
                }
            }
        }

    sealed interface LeaderboardFetchState {
        data object Loading : LeaderboardFetchState
        data class Success(
            val data: List<LeaderboardStandingDto>,
            val leaderboardData: LeaderboardDto
        ) : LeaderboardFetchState

        data class Error(val error: String) : LeaderboardFetchState
    }
}
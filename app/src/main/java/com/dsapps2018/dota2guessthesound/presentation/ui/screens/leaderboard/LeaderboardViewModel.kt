package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val leaderboardRepository: LeaderboardRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _leaderboardState =
        MutableStateFlow<LeaderboardFetchState>(LeaderboardFetchState.Loading)
    val leaderboardState = _leaderboardState.asStateFlow()

    private val _countdownFlow = MutableStateFlow("")
    val countdownFlow: StateFlow<String> = _countdownFlow.asStateFlow()

    val leaderboardMonth: StateFlow<String> = leaderboardRepository.getLeaderboardMonth().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val leaderboardId: StateFlow<Int> = leaderboardRepository.getLeaderboardId().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 1
    )

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
                firebaseCrashlytics.recordException(throwable)
                _leaderboardState.value =
                    LeaderboardFetchState.Error(context.getString(R.string.leaderboard_standing_fetch_error))
        }

    fun fetchLeaderboardStanding() = viewModelScope.launch(coroutineExceptionHandler) {
        val result = leaderboardRepository.fetchLeaderboardStanding()
        if (result.isNotEmpty()) {
            result[0].let { leaderboardStanding ->
                startCountdown(leaderboardStanding.endAt, leaderboardStanding.serverTimestamp)
            }
        }
        _leaderboardState.value = LeaderboardFetchState.Success(result)
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
                    _countdownFlow.value = "Time's up!"
                    break
                } else {

                    // Calculate remaining time
                    val days = remainingTime / (24 * 60 * 60 * 1000)
                    val hours = (remainingTime / (60 * 60 * 1000)) % 24
                    val minutes = (remainingTime / (60 * 1000)) % 60
                    val seconds = (remainingTime / 1000) % 60

                    val daysString = if(days <= 1) "day" else "days"
                    // Update only if the visible value changes
                    val newCountdownText = if (days > 0) String.format(
                        Locale.getDefault(),
                        "%02d $daysString %02dh:%02dm:%02ds",
                        days, hours, minutes, seconds
                    ) else String.format(
                        Locale.getDefault(),
                        "%02dh:%02dm:%02ds",
                        hours, minutes, seconds
                    )
                    if (_countdownFlow.value != newCountdownText) {
                        _countdownFlow.value = newCountdownText
                    }

                    // Delay until the next second boundary
                    val delayMillis = 1000 - (localNow.nano / 1_000_000)
                    delay(delayMillis.toLong())
                }
            }
        }

    sealed interface LeaderboardFetchState {
        data object Loading : LeaderboardFetchState
        data class Success(val data: List<LeaderboardStandingDto>) : LeaderboardFetchState
        data class Error(val error: String) : LeaderboardFetchState
    }
}
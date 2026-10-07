package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.data.progress.FirstClearOutcome
import com.dsapps2018.dota2guessthesound.data.progress.JourneyWinSession
import com.dsapps2018.dota2guessthesound.data.progress.ModeResult
import com.dsapps2018.dota2guessthesound.presentation.navigation.JourneyIssueGateDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyIssueGateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val modeResult: ModeResult,
    private val journeyWinSession: JourneyWinSession,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<JourneyIssueGateDestination>()
    val level: Int = route.level
    val clearTimeMs: Long = route.clearTimeMs

    private val _route = MutableStateFlow<GateRoute?>(null)
    val gateRoute = _route.asStateFlow()

    private var started = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        _route.value = GateRoute.Result
    }

    fun submitIfNeeded() {
        if (started) return
        started = true
        journeyWinSession.clear()
        viewModelScope.launch(exceptionHandler) {
            val win = modeResult.submitJourneyLevel(level, clearTimeMs)
            journeyWinSession.pending = win
            _route.value = when (val outcome = win.issueOutcome) {
                FirstClearOutcome.None -> GateRoute.Result
                FirstClearOutcome.ShowPicker -> GateRoute.Picker
                is FirstClearOutcome.ShowReader -> GateRoute.Reader(
                    issueId = outcome.issueId,
                    chapterIndex = outcome.chapterIndex,
                    unlockedCount = outcome.unlockedCount,
                )
            }
        }
    }

    sealed interface GateRoute {
        data object Result : GateRoute
        data object Picker : GateRoute
        data class Reader(
            val issueId: Int,
            val chapterIndex: Int,
            val unlockedCount: Int,
        ) : GateRoute
    }
}

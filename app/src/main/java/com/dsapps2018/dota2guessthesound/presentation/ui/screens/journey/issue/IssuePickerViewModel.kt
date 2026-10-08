package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.IssuePickerItem
import com.dsapps2018.dota2guessthesound.data.progress.IssueRating
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.dsapps2018.dota2guessthesound.data.progress.PickOutcome
import com.dsapps2018.dota2guessthesound.presentation.navigation.IssuePickerDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssuePickerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val journeyIssue: JourneyIssue,
    private val issueRating: IssueRating,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<IssuePickerDestination>()
    val level: Int = route.level
    val clearTimeMs: Long = route.clearTimeMs

    private val _uiState = MutableStateFlow<IssuePickerUiState>(IssuePickerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _nav = MutableStateFlow<PickerNav?>(null)
    val nav = _nav.asStateFlow()

    private var picking = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        picking = false
        _uiState.value = IssuePickerUiState.Error(
            context.getString(R.string.issue_picker_error)
        )
    }

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch(exceptionHandler) {
        _uiState.value = IssuePickerUiState.Loading
        issueRating.refreshScores()
        val items = journeyIssue.pickerItems()
        if (items.isEmpty()) {
            _uiState.value = IssuePickerUiState.Empty
            _nav.value = PickerNav.Result
        } else {
            _uiState.value = IssuePickerUiState.Ready(items)
        }
    }

    fun pick(issueId: Int) {
        if (picking) return
        picking = true
        viewModelScope.launch(exceptionHandler) {
            when (val outcome = journeyIssue.pickIssue(issueId)) {
                PickOutcome.ShowPickerAgain -> {
                    picking = false
                    _nav.value = null
                    refresh()
                }
                is PickOutcome.ShowReader -> {
                    _nav.value = PickerNav.Reader(
                        issueId = outcome.issueId,
                        chapterIndex = outcome.chapterIndex,
                        unlockedCount = outcome.unlockedCount,
                    )
                }
            }
        }
    }

    fun consumeNav() {
        _nav.value = null
    }

    sealed interface IssuePickerUiState {
        data object Loading : IssuePickerUiState
        data object Empty : IssuePickerUiState
        data class Ready(val items: List<IssuePickerItem>) : IssuePickerUiState
        data class Error(val message: String) : IssuePickerUiState
    }

    sealed interface PickerNav {
        data object Result : PickerNav
        data class Reader(
            val issueId: Int,
            val chapterIndex: Int,
            val unlockedCount: Int,
        ) : PickerNav
    }
}

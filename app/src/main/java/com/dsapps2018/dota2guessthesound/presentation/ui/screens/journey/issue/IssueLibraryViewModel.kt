package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.IssueLibraryItem
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueLibraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journeyIssue: JourneyIssue,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    private val _uiState = MutableStateFlow<IssueLibraryUiState>(IssueLibraryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        _uiState.value = IssueLibraryUiState.Error(
            context.getString(R.string.issue_library_error)
        )
    }

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch(exceptionHandler) {
        _uiState.value = IssueLibraryUiState.Loading
        val items = journeyIssue.libraryItems()
        _uiState.value = if (items.isEmpty()) {
            IssueLibraryUiState.Empty
        } else {
            IssueLibraryUiState.Ready(items)
        }
    }

    sealed interface IssueLibraryUiState {
        data object Loading : IssueLibraryUiState
        data object Empty : IssueLibraryUiState
        data class Ready(val items: List<IssueLibraryItem>) : IssueLibraryUiState
        data class Error(val message: String) : IssueLibraryUiState
    }
}

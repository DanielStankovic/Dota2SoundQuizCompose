package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.dsapps2018.dota2guessthesound.presentation.navigation.ReadingLocaleDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingLocaleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val journeyIssue: JourneyIssue,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ReadingLocaleDestination>()
    val fromOptions: Boolean = route.fromOptions

    private val _uiState = MutableStateFlow(
        ReadingLocaleUiState(
            locales = curatedReadingLocaleOptions(),
            selectedCode = JourneyIssue.ENGLISH,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _nav = MutableStateFlow<ReadingLocaleNav?>(null)
    val nav = _nav.asStateFlow()

    private var confirming = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        confirming = false
        _uiState.update {
            it.copy(
                saving = false,
                errorMessage = context.getString(R.string.reading_locale_error),
            )
        }
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            val current = journeyIssue.readingLocaleOrNull()
            if (!fromOptions && current != null) {
                _nav.value = ReadingLocaleNav.Done
                return@launch
            }
            _uiState.update {
                it.copy(selectedCode = current ?: JourneyIssue.ENGLISH)
            }
        }
    }

    fun select(code: String) {
        if (confirming) return
        _uiState.update {
            it.copy(selectedCode = code, errorMessage = null)
        }
    }

    fun confirm() {
        if (confirming) return
        confirming = true
        val code = _uiState.value.selectedCode
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(saving = true, errorMessage = null) }
            journeyIssue.setReadingLocale(code)
            confirming = false
            _uiState.update { it.copy(saving = false) }
            _nav.value = ReadingLocaleNav.Done
        }
    }

    fun consumeNav() {
        _nav.value = null
    }

    data class ReadingLocaleUiState(
        val locales: List<ReadingLocaleOption>,
        val selectedCode: String,
        val saving: Boolean = false,
        val errorMessage: String? = null,
    )

    sealed interface ReadingLocaleNav {
        data object Done : ReadingLocaleNav
    }
}

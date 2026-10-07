package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.ChapterReaderPage
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.navigation.ChapterReaderDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** How the Chapter reader exits — first-clear Continues to Mode Result; Library only closes. */
sealed interface ChapterReaderMode {
    data class FirstClear(val level: Int, val clearTimeMs: Long) : ChapterReaderMode
    data object Library : ChapterReaderMode
}

@HiltViewModel
class ChapterReaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val journeyIssue: JourneyIssue,
    private val sharedPreferences: SharedPreferences,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ChapterReaderDestination>()
    private val issueId: Int = route.issueId
    private val unlockedCount: Int = route.unlockedCount

    val mode: ChapterReaderMode = if (route.fromLibrary) {
        ChapterReaderMode.Library
    } else {
        ChapterReaderMode.FirstClear(route.level, route.clearTimeMs)
    }

    private val _uiState = MutableStateFlow<ChapterReaderUiState>(ChapterReaderUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _musicMuted = MutableStateFlow(
        sharedPreferences.getBoolean(Constants.PREF_STORY_MUSIC_MUTED, false)
    )
    val musicMuted = _musicMuted.asStateFlow()

    private var viewingIndex: Int = route.chapterIndex

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        _uiState.value = ChapterReaderUiState.Error(
            context.getString(R.string.chapter_reader_error)
        )
    }

    init {
        load(viewingIndex)
    }

    fun toggleMusicMuted() {
        val next = !_musicMuted.value
        _musicMuted.value = next
        sharedPreferences.edit().putBoolean(Constants.PREF_STORY_MUSIC_MUTED, next).apply()
    }

    fun previousChapter() {
        if (viewingIndex <= 1) return
        load(viewingIndex - 1)
    }

    fun nextChapter() {
        if (viewingIndex >= unlockedCount) return
        load(viewingIndex + 1)
    }

    private fun load(index: Int) = viewModelScope.launch(exceptionHandler) {
        // Keep the current page painted while swapping Chapters — only the first open shows Loading.
        if (_uiState.value !is ChapterReaderUiState.Ready) {
            _uiState.value = ChapterReaderUiState.Loading
        }
        val page = journeyIssue.chapterPage(issueId, index, unlockedCount)
        viewingIndex = index
        _uiState.value = if (page == null) {
            ChapterReaderUiState.Error(context.getString(R.string.chapter_reader_error))
        } else {
            ChapterReaderUiState.Ready(
                page = page,
                canGoPrevious = index > 1,
                canGoNext = index < unlockedCount,
            )
        }
    }

    sealed interface ChapterReaderUiState {
        data object Loading : ChapterReaderUiState
        data class Ready(
            val page: ChapterReaderPage,
            val canGoPrevious: Boolean,
            val canGoNext: Boolean,
        ) : ChapterReaderUiState
        data class Error(val message: String) : ChapterReaderUiState
    }
}

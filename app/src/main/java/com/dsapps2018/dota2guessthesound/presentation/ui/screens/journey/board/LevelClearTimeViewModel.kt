package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.board

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LevelClearTimeStandingDto
import com.dsapps2018.dota2guessthesound.data.progress.LevelClearTime
import com.dsapps2018.dota2guessthesound.presentation.navigation.LevelClearTimeDestination
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelClearTimeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val levelClearTime: LevelClearTime,
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ViewModel() {

    val level: Int = savedStateHandle.toRoute<LevelClearTimeDestination>().level

    private val _boardState = MutableStateFlow<LevelClearTimeBoardState>(
        LevelClearTimeBoardState.Loading
    )
    val boardState = _boardState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        firebaseCrashlytics.recordException(throwable)
        _boardState.value = LevelClearTimeBoardState.Error(
            context.getString(R.string.level_clear_time_fetch_error)
        )
    }

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch(exceptionHandler) {
        _boardState.value = LevelClearTimeBoardState.Loading
        val rows = levelClearTime.fetchBoard(level)
        _boardState.value = if (rows.isEmpty()) {
            LevelClearTimeBoardState.Empty
        } else {
            LevelClearTimeBoardState.Success(rows)
        }
    }

    sealed interface LevelClearTimeBoardState {
        data object Loading : LevelClearTimeBoardState
        data object Empty : LevelClearTimeBoardState
        data class Success(val rows: List<LevelClearTimeStandingDto>) : LevelClearTimeBoardState
        data class Error(val message: String) : LevelClearTimeBoardState
    }
}

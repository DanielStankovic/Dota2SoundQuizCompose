package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.auth.AuthSession
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.data.model.initialPlayerProgress
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.dsapps2018.dota2guessthesound.data.util.formatTimestampToLocalDateTime
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authSession: AuthSession,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val playerProgressRepository: PlayerProgressRepository,
) : ViewModel() {

    private val _authEventStatus = MutableSharedFlow<AuthEvent>()
    val authEventStatus = _authEventStatus.asSharedFlow()
    val authStatus = authSession.sessionStatus

    /** Profile still reads progress here; Home prefers [HomeViewModel]. */
    val progress: StateFlow<PlayerProgress> = playerProgressRepository.progress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initialPlayerProgress()
    )

    val modifiedDateFlow: StateFlow<String> = playerProgressRepository.lastSyncAt().map { date ->
        formatTimestampToLocalDateTime(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "--Data not synced--"
    )

    fun getNoncePair(): Pair<String, String> = authSession.noncePair()

    fun signInToSupabase(googleIdToken: String, rawNonce: String) {
        viewModelScope.launch {
            try {
                authSession.signInWithGoogle(googleIdToken, rawNonce)
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.login_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }

    fun syncUserData() {
        viewModelScope.launch {
            try {
                authSession.syncProgress()
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.sync_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.sync_error)))
            }
        }
    }

    fun onErrorException(e: Exception) {
        viewModelScope.launch {
            firebaseCrashlytics.recordException(e)
            _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
        }
    }

    fun onErrorEvent(msg: String) {
        viewModelScope.launch {
            _authEventStatus.emit(AuthEvent.Error(msg, true))
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authSession.signOut()
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.data.model.initialPlayerProgress
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.formatTimestampToLocalDateTime
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val playerProgressRepository: PlayerProgressRepository,
    private val leaderboardRepository: LeaderboardRepository,
) : ViewModel() {

    private val _authEventStatus = MutableSharedFlow<AuthEvent>()
    val authEventStatus = _authEventStatus.asSharedFlow()
    val authStatus = auth.sessionStatus

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

    fun getNoncePair(): Pair<String, String> {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return rawNonce to digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun signInToSupabase(googleIdToken: String, rawNonce: String) {
        viewModelScope.launch {
            try {
                auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

                auth.currentUserOrNull()?.id?.let { id ->
                    if (checkIfUserDataExists(id)) {
                        playerProgressRepository.sync()
                    } else {
                        playerProgressRepository.createServerUserData(id)
                        playerProgressRepository.sync()
                    }
                    leaderboardRepository.updateUserIdAndSendData(id)
                }
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.login_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }

    private suspend fun checkIfUserDataExists(userId: String): Boolean {
        val count = postgrest.from(Constants.TABLE_GAME_DATA).select(
            columns = Columns.list("user_id")
        ) {
            filter {
                eq("user_id", userId)
            }
            count(Count.EXACT)
        }.countOrNull()
        return !(count == null || count <= 0)
    }

    fun syncUserData() {
        viewModelScope.launch {
            try {
                playerProgressRepository.sync()
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
                playerProgressRepository.sync()
                auth.signOut()
                playerProgressRepository.resetLocalUserData()
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }
}

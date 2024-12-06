package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.getInitialUserData
import com.dsapps2018.dota2guessthesound.data.repository.ScoreRepository
import com.dsapps2018.dota2guessthesound.data.repository.UserDataRepository
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
    private val userDataRepository: UserDataRepository,
    scoreRepository: ScoreRepository

) : ViewModel() {

    private val _authEventStatus = MutableSharedFlow<AuthEvent>()
    val authEventStatus = _authEventStatus.asSharedFlow()
    val authStatus = auth.sessionStatus

    val userData: StateFlow<UserDataEntity> = scoreRepository.getUserDataFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getInitialUserData()
    )

    val modifiedDateFlow: StateFlow<String> = scoreRepository.getLastSyncDate().map { date ->
        formatTimestampToLocalDateTime(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "--Data not synced--"
    )

    fun getNoncePair(): Pair<String, String> {
        // Generate a nonce and hash it with sha-256
        // Providing a nonce is optional but recommended
        val rawNonce = UUID.randomUUID()
            .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
        val bytes = rawNonce.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return rawNonce to digest.fold("") { str, it -> str + "%02x".format(it) } // Hashed nonce to be passed to Google sign-in
    }

    fun signInToSupabase(googleIdToken: String, rawNonce: String) {
        viewModelScope.launch {
            try {
                //Sign In User
                auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

                //Get SignedIn user ID to perform game data insert or sync
                auth.currentUserOrNull()?.id?.let { id ->
                    if (checkIfUserDataExists(id)) {
                        userDataRepository.syncUserData()
                    } else {
                        userDataRepository.createServerUserData(id)
                        userDataRepository.syncUserData()
                    }
                }
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.login_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }

    private suspend fun checkIfUserDataExists(userId: String): Boolean {
        try {
            val count = postgrest.from(Constants.TABLE_GAME_DATA).select(
                columns = Columns.list("user_id")
            ) {
                filter {
                    eq("user_id", userId)
                }
                count(Count.EXACT)
            }.countOrNull()
            return !(count == null || count <= 0)
        } catch (e: Exception) {
            throw e
        }
    }

    fun syncUserData() {
        viewModelScope.launch {
            try {
                userDataRepository.syncUserData()
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.sync_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.sync_error)))
            }
        }
    }

    fun onErrorEvent(e: Exception) {
        viewModelScope.launch {
            firebaseCrashlytics.recordException(e)
            _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
            }
        }
    }
}
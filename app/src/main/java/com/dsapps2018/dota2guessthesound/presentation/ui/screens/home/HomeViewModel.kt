package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants.PERMISSION_CHECK_TAG
import com.dsapps2018.dota2guessthesound.data.util.Constants.TEN_DAYS_MILLIS
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile.AuthEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val auth: Auth,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _authEventStatus = MutableSharedFlow<AuthEvent>()
    val authEventStatus = _authEventStatus.asSharedFlow()

    fun setPermissionCheckTimestamp() {
        val currentTimestamp = System.currentTimeMillis()
        sharedPreferences.edit().putLong(PERMISSION_CHECK_TAG, currentTimestamp).apply()
    }

    val authStatus = auth.sessionStatus

    fun shouldShowRationaleAgain(): Boolean {
        val savedTimestamp =
            sharedPreferences.getLong(PERMISSION_CHECK_TAG, -1L)

        if (savedTimestamp == -1L) return true
        val currentTimestamp = System.currentTimeMillis()

        return (currentTimestamp - savedTimestamp) >= TEN_DAYS_MILLIS
    }

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
                auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }
                _authEventStatus.emit(AuthEvent.Success(context.getString(R.string.login_success)))
            } catch (e: Exception) {
                firebaseCrashlytics.recordException(e)
                _authEventStatus.emit(AuthEvent.Error(context.getString(R.string.login_error)))
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
        viewModelScope.launch{
            auth.signOut()
        }
    }
}
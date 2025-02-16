package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.repository.ConfigRepository
import com.dsapps2018.dota2guessthesound.data.util.Constants.FORCED_VERSION_TAG
import com.dsapps2018.dota2guessthesound.data.util.Constants.PERMISSION_CHECK_TAG
import com.dsapps2018.dota2guessthesound.data.util.Constants.TEN_DAYS_MILLIS
import com.dsapps2018.dota2guessthesound.data.util.Constants.THREE_DAYS_MILLIS
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val configRepository: ConfigRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val soundPlayer: SoundPlayer,
) : ViewModel() {

    private val _updateRequiredStatus = MutableStateFlow<Boolean>(false)
    val updateRequiredStatus = _updateRequiredStatus.asStateFlow()

    companion object {
        private const val MAX_INDEX = 4
    }

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            viewModelScope.launch {
                firebaseCrashlytics.recordException(throwable)
            }
        }

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    fun moveLeft() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun moveRight() {
        if (_currentIndex.value < MAX_INDEX) {
            _currentIndex.value += 1
        }
    }

    fun setPermissionCheckTimestamp() {
        val currentTimestamp = System.currentTimeMillis()
        sharedPreferences.edit().putLong(PERMISSION_CHECK_TAG, currentTimestamp).apply()
    }

    fun shouldShowRationaleAgain(): Boolean {
        val savedTimestamp = sharedPreferences.getLong(PERMISSION_CHECK_TAG, -1L)

        if (savedTimestamp == -1L) return true
        val currentTimestamp = System.currentTimeMillis()

        return (currentTimestamp - savedTimestamp) >= TEN_DAYS_MILLIS
    }

    fun moveToJokeIndex() {
        _currentIndex.value = 4
    }

    fun playJokeSound() {
        val resourceId = R.raw.pudge_joke
        val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
        soundPlayer.playSoundFromResource(uri)
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.stop()
    }

    fun checkForcedVersion() {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (!shouldCheckForcedVersion()) return@launch
            val configDto = configRepository.getRemoteConfig()
            setForcedVersionCheckTimestamp()
            if (configDto.forcedVersion > BuildConfig.VERSION_CODE) {
                _updateRequiredStatus.value = true
                return@launch
            }
        }
    }

    private fun shouldCheckForcedVersion(): Boolean {
        val savedTimestamp = sharedPreferences.getLong(FORCED_VERSION_TAG, -1L)
        if (savedTimestamp == -1L) return true
        val currentTimestamp = System.currentTimeMillis()
        return (currentTimestamp - savedTimestamp) >= THREE_DAYS_MILLIS
    }

    private fun setForcedVersionCheckTimestamp() {
        val currentTimestamp = System.currentTimeMillis()
        sharedPreferences.edit().putLong(FORCED_VERSION_TAG, currentTimestamp).apply()
    }

}
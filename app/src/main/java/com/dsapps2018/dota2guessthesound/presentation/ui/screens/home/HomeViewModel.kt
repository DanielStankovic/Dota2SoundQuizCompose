package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants.PERMISSION_CHECK_TAG
import com.dsapps2018.dota2guessthesound.data.util.Constants.TEN_DAYS_MILLIS
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val soundPlayer: SoundPlayer,
) : ViewModel() {

    companion object {
        private const val MAX_INDEX = 3
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
        val savedTimestamp =
            sharedPreferences.getLong(PERMISSION_CHECK_TAG, -1L)

        if (savedTimestamp == -1L) return true
        val currentTimestamp = System.currentTimeMillis()

        return (currentTimestamp - savedTimestamp) >= TEN_DAYS_MILLIS
    }

    fun moveToJokeIndex() {
        _currentIndex.value = 3
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

}
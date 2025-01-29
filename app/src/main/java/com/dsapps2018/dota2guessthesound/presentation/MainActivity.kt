package com.dsapps2018.dota2guessthesound.presentation


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.dsapps2018.dota2guessthesound.data.admob.loadInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.loadRewardedAd
import com.dsapps2018.dota2guessthesound.data.admob.removeInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.removeRewarded
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.dsapps2018.dota2guessthesound.presentation.navigation.HomeNavGraph
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Dota2SoundQuizComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var soundPlayer: SoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterstitial(this)
        loadRewardedAd(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            Dota2SoundQuizComposeTheme {
                HomeNavGraph()
            }
        }
    }

    override fun onDestroy() {
        removeInterstitial()
        removeRewarded()
        soundPlayer.release()
        super.onDestroy()
    }
}
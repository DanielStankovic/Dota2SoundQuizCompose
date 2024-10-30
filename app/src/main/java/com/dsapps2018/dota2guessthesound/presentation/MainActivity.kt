package com.dsapps2018.dota2guessthesound.presentation


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.dsapps2018.dota2guessthesound.presentation.navigation.HomeNavGraph
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Dota2SoundQuizComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            Dota2SoundQuizComposeTheme {
                    HomeNavGraph()
            }
        }
    }
}
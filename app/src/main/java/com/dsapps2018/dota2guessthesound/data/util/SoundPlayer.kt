package com.dsapps2018.dota2guessthesound.data.util

import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(
    private val context: Context,
) {
    private var mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }

    fun reset() = mediaPlayer.reset()

    fun stop() = mediaPlayer.stop()

    fun playSound(path: String) {
        mediaPlayer.run {
            reset()
            setDataSource(
               path
            )
            prepareAsync()
        }
    }
}
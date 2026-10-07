package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dsapps2018.dota2guessthesound.R

/**
 * Loops the bundled Chapter-reader music bed while this composition is active.
 * Pauses on [Lifecycle.Event.ON_STOP], resumes on [Lifecycle.Event.ON_START],
 * and always [MediaPlayer.release]s on dispose.
 */
@Composable
fun ChapterReaderMusicBed() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        var wantPlaying =
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        val player = createLoopingBed(context) { wantPlaying }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    wantPlaying = true
                    player.safeStart()
                }
                Lifecycle.Event.ON_STOP -> {
                    wantPlaying = false
                    player.safePause()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            wantPlaying = false
            player?.run {
                setOnPreparedListener(null)
                safeStop()
                release()
            }
        }
    }
}

private const val MUSIC_BED_VOLUME = 0.4f

/**
 * @param shouldPlay called when async prepare finishes — must not start if the host already stopped.
 */
private fun createLoopingBed(
    context: Context,
    shouldPlay: () -> Boolean,
): MediaPlayer? {
    return try {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            isLooping = true
            setVolume(MUSIC_BED_VOLUME, MUSIC_BED_VOLUME)
            context.resources.openRawResourceFd(R.raw.story_music_bed).use { afd ->
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            setOnPreparedListener { player ->
                if (shouldPlay()) {
                    try {
                        player.start()
                    } catch (_: IllegalStateException) {
                        // Released between prepare and callback.
                    }
                }
            }
            prepareAsync()
        }
    } catch (_: Exception) {
        null
    }
}

private fun MediaPlayer?.safeStart() {
    val player = this ?: return
    try {
        if (!player.isPlaying) player.start()
    } catch (_: IllegalStateException) {
        // Not prepared yet / already released — prepare listener will start when ready.
    }
}

private fun MediaPlayer?.safePause() {
    val player = this ?: return
    try {
        if (player.isPlaying) player.pause()
    } catch (_: IllegalStateException) {
        // Already released.
    }
}

private fun MediaPlayer.safeStop() {
    try {
        stop()
    } catch (_: IllegalStateException) {
        // Not started / already stopped — still release below.
    }
}

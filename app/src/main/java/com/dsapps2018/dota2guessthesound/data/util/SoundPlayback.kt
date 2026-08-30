package com.dsapps2018.dota2guessthesound.data.util

import android.content.Context
import android.net.Uri
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sound Playback: play spell sounds (or a raw resource) without callers knowing mapper/Uri details.
 * See docs/adr/0004-sound-playback.md.
 */
@Singleton
class SoundPlayback @Inject constructor(
    @ApplicationContext private val context: Context,
    private val soundPlayer: SoundPlayer,
) {

    fun play(sound: SoundModel) {
        if (sound.isLocal) {
            val resourceId = SoundFileMapper.map[sound.spellName] ?: return
            playRaw(resourceId)
        } else if (sound.soundFileLink.isNotEmpty()) {
            soundPlayer.playSound(sound.soundFileLink)
        }
    }

    fun playRaw(resourceId: Int) {
        val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
        soundPlayer.playSoundFromResource(uri)
    }

    fun stop() = soundPlayer.stop()
}

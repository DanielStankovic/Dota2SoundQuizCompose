package com.dsapps2018.dota2guessthesound.data.util

import android.content.Context
import android.net.Uri
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sound Playback: play a [SoundModel] without callers knowing mapper/Uri details.
 * See docs/adr/0003-journey-round.md — Journey Round is the first caller; other modes retarget later.
 */
@Singleton
class SoundPlayback @Inject constructor(
    @ApplicationContext private val context: Context,
    private val soundPlayer: SoundPlayer,
) {

    fun play(sound: SoundModel) {
        if (sound.isLocal) {
            val resourceId = SoundFileMapper.map[sound.spellName] ?: return
            val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
            soundPlayer.playSoundFromResource(uri)
        } else if (sound.soundFileLink.isNotEmpty()) {
            soundPlayer.playSound(sound.soundFileLink)
        }
    }

    fun stop() = soundPlayer.stop()
}

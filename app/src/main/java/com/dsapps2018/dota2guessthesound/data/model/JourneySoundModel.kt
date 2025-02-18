package com.dsapps2018.dota2guessthesound.data.model

import androidx.room.Embedded

data class JourneySoundModel(
    @Embedded
    val soundModel: SoundModel,
    val isCorrectSound: Boolean
)

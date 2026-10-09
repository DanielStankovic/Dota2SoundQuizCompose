package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JourneyAssistUnlockDto(
    val level: Int,
    @SerialName("count_unlocked")
    val countUnlocked: Boolean = false,
    @SerialName("primer_unlocked")
    val primerUnlocked: Boolean = false,
)

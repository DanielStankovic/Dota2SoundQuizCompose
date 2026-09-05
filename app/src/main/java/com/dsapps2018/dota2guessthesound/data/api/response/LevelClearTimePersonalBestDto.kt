package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelClearTimePersonalBestDto(
    val level: Int,
    @SerialName("clear_time_ms")
    val clearTimeMs: Long,
)

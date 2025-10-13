package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JourneyLevelDto(
    @SerialName("id")
    val id: Int,
    @SerialName("level")
    val level: Int
)

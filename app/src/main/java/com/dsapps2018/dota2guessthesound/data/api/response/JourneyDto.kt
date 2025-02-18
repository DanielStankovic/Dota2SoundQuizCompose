package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JourneyDto(
    @SerialName("id")
    val id: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("radiant_heroes")
    val radiantHeroes: List<Int>,
    @SerialName("dire_heroes")
    val direHeroes: List<Int>,
    @SerialName("max_sounds")
    val maxSounds: Int
)

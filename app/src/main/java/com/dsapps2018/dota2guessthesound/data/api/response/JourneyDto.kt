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
    val maxSounds: Int,
    @SerialName("affixes")
    val affixes: List<Int>,
    /**
     * Hero ids whose portraits should show `?` when Partial Veil is active.
     * Keyed by hero id so Dire row can reuse this later.
     */
    @SerialName("masked_hero_ids")
    val maskedHeroIds: List<Int> = emptyList(),
    /**
     * Hero ids whose portraits get Blurred Vision blur when that Affix is active.
     * Empty = blur nobody. Keyed by hero id so Dire row can reuse this later.
     */
    @SerialName("blurred_hero_ids")
    val blurredHeroIds: List<Int> = emptyList(),
    /**
     * Race countdown / later Soundquake interval (seconds).
     * Null → fall back to Affix `data.timer`, then 60.
     */
    @SerialName("timer_seconds")
    val timerSeconds: Int? = null,
    /**
     * Race Extra Life Gate time buyback (seconds). Default 20 when unset/non-positive.
     */
    @SerialName("timer_extension_seconds")
    val timerExtensionSeconds: Int? = null,
    /**
     * Echo Limit: extra plays beyond [maxSounds]. Effective = maxSounds + offset.
     * Null/missing → Medium default (+5). `0` = Hard. Used only when Echo Limit Affix is on.
     */
    @SerialName("echo_limit_offset")
    val echoLimitOffset: Int? = null,
)

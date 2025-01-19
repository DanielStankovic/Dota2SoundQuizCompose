package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigDto(
    @SerialName("forced_version")
    val forcedVersion: Int,
    @SerialName("delete_version")
    val deleteVersion: Int,
    @SerialName("recommended_version")
    val recommendedVersion: Int
)

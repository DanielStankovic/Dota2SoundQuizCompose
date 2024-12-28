package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameModeDto(
    @SerialName("id")
    val id : Int,
    @SerialName("mode")
    val mode: String,
    @SerialName("code")
    val code: String,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean
)

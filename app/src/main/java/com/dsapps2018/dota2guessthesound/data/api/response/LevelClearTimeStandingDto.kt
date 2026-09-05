package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelClearTimeStandingDto(
    @SerialName("standing")
    val standing: Long,
    @SerialName("clear_time_ms")
    val clearTimeMs: Long,
    @SerialName("user_name")
    val userName: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("is_current_user")
    val isCurrentUser: Boolean,
)

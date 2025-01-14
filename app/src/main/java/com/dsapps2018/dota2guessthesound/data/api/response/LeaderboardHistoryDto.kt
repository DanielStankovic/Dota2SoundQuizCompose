package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardHistoryDto(
    @SerialName("id")
    val id: Int,
    @SerialName("start_at")
    val startAt: String
)

package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardDto(
    @SerialName("id")
    val id: Int,
    @SerialName("start_at")
    val startAt: String,
    @SerialName("end_at")
    val endAt: String,
    @SerialName("current_server_timestamp")
    val serverTimestamp: String
)

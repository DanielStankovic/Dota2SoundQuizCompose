package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardStandingDto(
    @SerialName("standing")
    val standing : Int,
    @SerialName("total_score")
    val score: Double,
    @SerialName("user_name")
    val userName: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("end_at")
    val endAt: String,
    @SerialName("current_server_timestamp")
    val serverTimestamp: String
)

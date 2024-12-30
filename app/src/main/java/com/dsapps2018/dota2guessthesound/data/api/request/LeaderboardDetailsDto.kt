package com.dsapps2018.dota2guessthesound.data.api.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardDetailsDto(
    @SerialName("leaderboard_id")
    val leaderboardId: Int,
    @SerialName("user_id")
    val userId: String,
    @SerialName("game_mode_id")
    val gameModeId: Int,
    @SerialName("score")
    val score: Double
)

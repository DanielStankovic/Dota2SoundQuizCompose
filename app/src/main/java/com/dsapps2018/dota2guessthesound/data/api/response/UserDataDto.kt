package com.dsapps2018.dota2guessthesound.data.api.response

import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDataDto(
    @SerialName("user_id")
    var userId: String,
    @SerialName("quiz_score")
    var quizScore: Int,
    @SerialName("quiz_played")
    var quizPlayed: Int,
    @SerialName("invoker_score")
    var invokerScore: Int,
    @SerialName("invoker_played")
    var invokerPlayed: Int,
    @SerialName("thirty_score")
    var thirtySecondsScore: Double,
    @SerialName("thirty_played")
    var thirtyPlayed: Int,
    @SerialName("sixty_score")
    var sixtySecondsScore: Double,
    @SerialName("sixty_played")
    var sixtyPlayed: Int,
    @SerialName("ninety_score")
    var ninetySecondsScore: Double,
    @SerialName("ninety_played")
    var ninetyPlayed: Int,
    @SerialName("coin_value")
    var coinValue: Int,
    @SerialName("modified_at")
    var modifiedAt: String,
)

fun getInitialServerUserData(userId: String): UserDataDto{
    return UserDataDto(
        userId = userId,
        quizScore = 0,
        quizPlayed = 0,
        invokerScore = 0,
        invokerPlayed = 0,
        thirtySecondsScore = 0.0,
        thirtyPlayed = 0,
        sixtySecondsScore = 0.0,
        sixtyPlayed = 0,
        ninetySecondsScore = 0.0,
        ninetyPlayed = 0,
        coinValue = 0,
        modifiedAt = getCurrentDate()
    )
}

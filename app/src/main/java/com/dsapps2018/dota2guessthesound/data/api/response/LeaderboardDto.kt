package com.dsapps2018.dota2guessthesound.data.api.response

import com.dsapps2018.dota2guessthesound.data.util.getMonthStringFromStringDate
import com.dsapps2018.dota2guessthesound.data.util.getMonthYearStringFromStringDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String?,
    @SerialName("start_at")
    val startAt: String,
    @SerialName("end_at")
    val endAt: String,
    @SerialName("current_server_timestamp")
    val serverTimestamp: String
) {
    fun getLeaderboardName(includeYear: Boolean): String {
        return name
            ?: if (includeYear) getMonthYearStringFromStringDate(startAt) else getMonthStringFromStringDate(
                startAt
            )
    }
}

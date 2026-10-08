package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryIssueScoreDto(
    @SerialName("id")
    val id: Int,
    @SerialName("rating_sum")
    val ratingSum: Long = 0L,
    @SerialName("rating_count")
    val ratingCount: Int = 0,
)

@Serializable
data class StoryIssueRatingDto(
    @SerialName("issue_id")
    val issueId: Int,
    @SerialName("stars")
    val stars: Int,
)

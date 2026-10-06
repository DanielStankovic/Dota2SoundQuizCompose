package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryIssueI18nDto(
    @SerialName("issue_id")
    val issueId: Int,
    @SerialName("locale")
    val locale: String,
    @SerialName("title")
    val title: String,
    @SerialName("synopsis")
    val synopsis: String,
    @SerialName("modified_at")
    val modifiedAt: String,
)

package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryProgressDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("active_issue_id")
    val activeIssueId: Int? = null,
    @SerialName("active_unlocked")
    val activeUnlocked: Int,
    @SerialName("owed_grants")
    val owedGrants: Int,
    @SerialName("completed_issue_ids")
    val completedIssueIds: List<Int> = emptyList(),
    @SerialName("reading_locale")
    val readingLocale: String? = null,
    @SerialName("modified_at")
    val modifiedAt: String,
)

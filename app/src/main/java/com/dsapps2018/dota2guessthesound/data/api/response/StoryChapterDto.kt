package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryChapterDto(
    @SerialName("id")
    val id: Int,
    @SerialName("issue_id")
    val issueId: Int,
    @SerialName("chapter_index")
    val chapterIndex: Int,
    @SerialName("modified_at")
    val modifiedAt: String,
)

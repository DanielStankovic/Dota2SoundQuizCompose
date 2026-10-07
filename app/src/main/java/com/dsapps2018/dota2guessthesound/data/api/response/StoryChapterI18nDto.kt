package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryChapterI18nDto(
    @SerialName("chapter_id")
    val chapterId: Int,
    @SerialName("locale")
    val locale: String,
    @SerialName("title")
    val title: String = "",
    @SerialName("body")
    val body: String,
    @SerialName("modified_at")
    val modifiedAt: String,
)

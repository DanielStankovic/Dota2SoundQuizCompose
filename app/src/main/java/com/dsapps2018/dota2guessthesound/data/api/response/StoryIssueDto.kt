package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryIssueDto(
    @SerialName("id")
    val id: Int,
    @SerialName("slug")
    val slug: String,
    @SerialName("protagonist_caster_id")
    val protagonistCasterId: Int,
    @SerialName("sort")
    val sort: Int,
    @SerialName("cover_path")
    val coverPath: String,
    @SerialName("backdrop_path")
    val backdropPath: String,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean,
)

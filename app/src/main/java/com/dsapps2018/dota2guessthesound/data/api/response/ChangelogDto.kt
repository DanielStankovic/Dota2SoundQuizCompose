package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangelogDto(
    @SerialName("id")
    val id : Int,
    @SerialName("version")
    val version: String,
    @SerialName("log")
    val log: List<String>,
    @SerialName("modified_at")
    val modifiedAt: String
)

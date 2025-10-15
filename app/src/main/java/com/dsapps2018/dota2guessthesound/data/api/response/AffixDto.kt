package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AffixDto(
    @SerialName("id")
    val id: Int,
    @SerialName("affix")
    val affix: String,
    @SerialName("description")
    val description: String,
    @SerialName("data")
    val data: JsonElement,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean
)

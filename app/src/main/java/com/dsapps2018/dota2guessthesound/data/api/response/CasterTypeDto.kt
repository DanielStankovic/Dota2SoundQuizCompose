package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CasterTypeDto(
    @SerialName("id")
    val id : Int,
    @SerialName("type")
    val type: String,
    @SerialName("code")
    val code: String,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean
)

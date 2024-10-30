package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CasterDto(
    @SerialName("id")
    val id : Int,
    @SerialName("name")
    val name: String,
    @SerialName("caster_type_id")
    val casterTypeId: Int,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean
)

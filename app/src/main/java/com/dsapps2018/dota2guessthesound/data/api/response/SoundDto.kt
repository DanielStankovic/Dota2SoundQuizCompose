package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundDto(
    @SerialName("id")
    val id : Int,
    @SerialName("spell_name")
    val spellName: String,
    @SerialName("sound_file_name")
    val soundFileName: String,
    @SerialName("sound_file_link")
    val soundFileLink: String,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("caster_id")
    val casterId : Int,
    @SerialName("active")
    val isActive: Boolean
)

package com.dsapps2018.dota2guessthesound.data.model

import kotlinx.serialization.json.JsonObject

data class AffixModel(
    val id: Int,
    val affix: String,
    val description: String,
    val iconResourceId: Int,
    val data: JsonObject
)

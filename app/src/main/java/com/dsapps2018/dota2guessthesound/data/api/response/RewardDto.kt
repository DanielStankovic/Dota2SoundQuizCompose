package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardDto(
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("item_image_url")
    val itemImageUrl: String,
    @SerialName("item_url")
    val itemUrl: String,
    @SerialName("item_name")
    val itemName: String,
    @SerialName("standing")
    val standing: Int,
    @SerialName("hero")
    val hero: String,
    @SerialName("item_type")
    val itemType: String,
)

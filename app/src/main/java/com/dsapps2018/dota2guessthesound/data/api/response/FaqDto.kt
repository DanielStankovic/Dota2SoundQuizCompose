package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FaqDto(
    @SerialName("id")
    val id: Int,
    @SerialName("question")
    val question: String,
    @SerialName("answer")
    val answers: List<AnswerDto>,
    @SerialName("question_order")
    val questionOrder: Int,
    @SerialName("modified_at")
    val modifiedAt: String,
    @SerialName("active")
    val isActive: Boolean
)

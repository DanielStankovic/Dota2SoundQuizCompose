package com.dsapps2018.dota2guessthesound.data.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AnswerDto(
    @SerialName("type")
    val type: String,
    @SerialName("answer_text")
    val answerText: String
)

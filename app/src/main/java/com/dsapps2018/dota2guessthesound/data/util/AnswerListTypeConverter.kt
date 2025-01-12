package com.dsapps2018.dota2guessthesound.data.util

import androidx.room.TypeConverter
import com.dsapps2018.dota2guessthesound.data.api.response.AnswerDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AnswerListTypeConverter {

    @TypeConverter
    fun fromList(value : List<AnswerDto>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<AnswerDto>>(value)
}
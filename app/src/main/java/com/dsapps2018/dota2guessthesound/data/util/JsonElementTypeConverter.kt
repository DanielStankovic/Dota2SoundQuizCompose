package com.dsapps2018.dota2guessthesound.data.util

import androidx.room.TypeConverter
import com.dsapps2018.dota2guessthesound.data.api.response.AnswerDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

class JsonElementTypeConverter {

    @TypeConverter
    fun fromJsonElement(value : JsonElement) = value.toString()

    @TypeConverter
    fun toJsonElement(value: String) = Json.encodeToJsonElement(value)
}
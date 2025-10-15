package com.dsapps2018.dota2guessthesound.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class JsonElementTypeConverter {

    @TypeConverter
    fun fromJsonObject(value : JsonObject) = value.toString()

    @TypeConverter
    fun toJsonObject(value: String) = Json.parseToJsonElement(value).jsonObject
}
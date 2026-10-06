package com.dsapps2018.dota2guessthesound.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IntListTypeConverter {

    @TypeConverter
    fun fromList(value: List<Int>): String = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String): List<Int> =
        if (value.isBlank()) emptyList() else Json.decodeFromString(value)
}

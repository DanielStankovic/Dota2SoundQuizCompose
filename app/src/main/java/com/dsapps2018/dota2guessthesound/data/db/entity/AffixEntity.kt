package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter
import com.dsapps2018.dota2guessthesound.data.util.JsonElementTypeConverter
import kotlinx.serialization.json.JsonObject

@Entity(tableName = "Affix")
data class AffixEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val affix: String,
    val description: String,
    @field:TypeConverters(JsonElementTypeConverter::class)
    val data: JsonObject,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val isActive: Boolean
)


package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter
import com.dsapps2018.dota2guessthesound.data.util.StringListTypeConverter

@Entity(tableName = "Changelog")
data class ChangelogEntity(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val version: String,
    @field:TypeConverters(StringListTypeConverter::class)
    val log: List<String>
)

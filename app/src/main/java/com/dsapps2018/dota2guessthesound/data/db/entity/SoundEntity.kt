package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "Sound")
data class SoundEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val spellName: String,
    val soundFileName: String,
    var soundFileLink: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val casterId: Int,
    val isActive: Boolean,
    @ColumnInfo(defaultValue = "1")
    val isLocal: Boolean
)
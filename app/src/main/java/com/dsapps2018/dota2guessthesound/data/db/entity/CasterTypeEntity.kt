package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "CasterType")
data class CasterTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val type: String,
    val code: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val isActive: Boolean
)

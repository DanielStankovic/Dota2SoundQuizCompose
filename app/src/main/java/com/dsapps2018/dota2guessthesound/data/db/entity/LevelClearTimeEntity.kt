package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LevelClearTime")
data class LevelClearTimeEntity(
    @PrimaryKey
    val level: Int,
    val clearTimeMs: Long,
    val isSent: Boolean,
)

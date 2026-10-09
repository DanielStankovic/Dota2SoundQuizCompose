package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "JourneyAssistUnlock")
data class JourneyAssistUnlockEntity(
    @PrimaryKey
    val level: Int,
    val countUnlocked: Boolean,
    /** Room column keeps legacy `primerUnlocked` name (synced as `primer_unlocked`). */
    @ColumnInfo(name = "primerUnlocked")
    val soundSamplerUnlocked: Boolean,
    val isSent: Boolean,
)

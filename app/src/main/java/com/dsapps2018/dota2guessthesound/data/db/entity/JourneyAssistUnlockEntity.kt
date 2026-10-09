package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "JourneyAssistUnlock")
data class JourneyAssistUnlockEntity(
    @PrimaryKey
    val level: Int,
    val countUnlocked: Boolean,
    val primerUnlocked: Boolean,
    val isSent: Boolean,
)

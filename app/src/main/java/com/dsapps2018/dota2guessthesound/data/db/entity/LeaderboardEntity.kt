package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "Leaderboard")
data class LeaderboardEntity (
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    @field:TypeConverters(DateTypeConverter::class)
    val startAt: String,
    @field:TypeConverters(DateTypeConverter::class)
    val endAt: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
)
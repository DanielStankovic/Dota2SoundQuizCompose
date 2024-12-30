package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "LeaderboardDetails")
data class LeaderboardDetailsEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val leaderboardId: Int,
    val gameModeId: Int,
    val userId: String?,
    val score: Double,
    @field:TypeConverters(DateTypeConverter::class)
    val createdDate: String,
    var isSent: Boolean
)
package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.IntListTypeConverter

@Entity(tableName = "StoryProgress")
data class StoryProgressEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val activeIssueId: Int?,
    val activeUnlocked: Int,
    val owedGrants: Int,
    @field:TypeConverters(IntListTypeConverter::class)
    val completedIssueIds: List<Int>,
    /** Null until the player confirms a Reading Locale. */
    val readingLocale: String?,
    val modifiedAt: String,
)

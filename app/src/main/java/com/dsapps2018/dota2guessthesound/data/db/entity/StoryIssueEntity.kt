package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "StoryIssue")
data class StoryIssueEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val slug: String,
    val protagonistCasterId: Int,
    val sort: Int,
    val coverPath: String,
    val backdropPath: String,
    var coverFileLink: String,
    var backdropFileLink: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val isActive: Boolean,
)

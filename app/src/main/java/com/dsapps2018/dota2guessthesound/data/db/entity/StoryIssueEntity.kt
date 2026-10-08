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
    /** Sum of all Issue Ratings for this Issue (Issue Score numerator). */
    val ratingSum: Long = 0L,
    /** Vote count backing [ratingSum] (Issue Score denominator). */
    val ratingCount: Int = 0,
)

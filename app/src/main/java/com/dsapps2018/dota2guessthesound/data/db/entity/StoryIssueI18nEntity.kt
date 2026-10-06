package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "StoryIssueI18n", primaryKeys = ["issueId", "locale"])
data class StoryIssueI18nEntity(
    val issueId: Int,
    val locale: String,
    val title: String,
    val synopsis: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
)

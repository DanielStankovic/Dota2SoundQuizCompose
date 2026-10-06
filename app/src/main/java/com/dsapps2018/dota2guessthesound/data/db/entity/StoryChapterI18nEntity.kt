package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "StoryChapterI18n", primaryKeys = ["chapterId", "locale"])
data class StoryChapterI18nEntity(
    val chapterId: Int,
    val locale: String,
    val body: String,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
)

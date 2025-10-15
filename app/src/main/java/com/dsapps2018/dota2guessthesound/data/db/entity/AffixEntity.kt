package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.api.response.AnswerDto
import com.dsapps2018.dota2guessthesound.data.util.AnswerListTypeConverter
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter

@Entity(tableName = "Faq")
data class FaqEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val questionOrder: Int,
    val question: String,
    @field:TypeConverters(AnswerListTypeConverter::class)
    val answers: List<AnswerDto>,
    @field:TypeConverters(DateTypeConverter::class)
    val modifiedAt: String,
    val isActive: Boolean
)


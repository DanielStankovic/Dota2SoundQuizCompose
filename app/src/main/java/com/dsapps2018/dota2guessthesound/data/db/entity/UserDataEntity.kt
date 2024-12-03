package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.DateTypeConverter
import com.dsapps2018.dota2guessthesound.data.util.getInitialModifiedDate

@Entity(tableName = "UserData")
data class UserDataEntity(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    var userId: String? = null,
    var quizScore: Int,
    var quizPlayed: Int,
    var invokerScore: Int,
    var invokerPlayed: Int,
    var thirtySecondsScore: Double,
    var thirtyPlayed: Int,
    var sixtySecondsScore: Double,
    var sixtyPlayed: Int,
    var ninetySecondsScore: Double,
    var ninetyPlayed: Int,
    @field:TypeConverters(DateTypeConverter::class)
    var modifiedAt: String,
    var isFresh: Boolean
)

fun getInitialUserData(): UserDataEntity{
    return UserDataEntity(
        id = Constants.USER_DATA_ID,
        quizScore = 0,
        quizPlayed = 0,
        invokerScore = 0,
        invokerPlayed = 0,
        thirtySecondsScore = 0.0,
        thirtyPlayed = 0,
        sixtySecondsScore = 0.0,
        sixtyPlayed = 0,
        ninetySecondsScore = 0.0,
        ninetyPlayed = 0,
        modifiedAt = getInitialModifiedDate(),
        isFresh = true
    )
}
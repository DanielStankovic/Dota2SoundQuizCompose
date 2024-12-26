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
    var syncedQuizPlayed: Int,
    var invokerScore: Int,
    var invokerPlayed: Int,
    var syncedInvokerPlayed: Int,
    var thirtySecondsScore: Double,
    var thirtyPlayed: Int,
    var syncedThirtyPlayed: Int,
    var sixtySecondsScore: Double,
    var sixtyPlayed: Int,
    var syncedSixtyPlayed: Int,
    var ninetySecondsScore: Double,
    var ninetyPlayed: Int,
    var syncedNinetyPlayed: Int,
    var coinValue: Int,
    var syncedCoinValue: Int,
    @field:TypeConverters(DateTypeConverter::class)
    var modifiedAt: String,
    var lastSyncAt: Long
)

fun getInitialUserData(): UserDataEntity{
    return UserDataEntity(
        id = Constants.USER_DATA_ID,
        quizScore = 0,
        quizPlayed = 0,
        syncedQuizPlayed = 0,
        invokerScore = 0,
        invokerPlayed = 0,
        syncedInvokerPlayed = 0,
        thirtySecondsScore = 0.0,
        thirtyPlayed = 0,
        syncedThirtyPlayed = 0,
        sixtySecondsScore = 0.0,
        sixtyPlayed = 0,
        syncedSixtyPlayed = 0,
        ninetySecondsScore = 0.0,
        ninetyPlayed = 0,
        syncedNinetyPlayed = 0,
        coinValue = 0,
        syncedCoinValue = 0,
        modifiedAt = getInitialModifiedDate(),
        lastSyncAt = System.currentTimeMillis()
    )
}
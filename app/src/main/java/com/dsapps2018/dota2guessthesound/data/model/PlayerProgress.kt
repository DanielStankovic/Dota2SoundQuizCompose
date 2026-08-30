package com.dsapps2018.dota2guessthesound.data.model

/**
 * Read model for Player Progress (non-Room). Mapped from local UserData storage.
 */
data class PlayerProgress(
    val quizScore: Int,
    val quizPlayed: Int,
    val invokerScore: Int,
    val invokerPlayed: Int,
    val thirtySecondsScore: Double,
    val thirtyPlayed: Int,
    val sixtySecondsScore: Double,
    val sixtyPlayed: Int,
    val ninetySecondsScore: Double,
    val ninetyPlayed: Int,
    val coinValue: Int,
    val journeyLevel: Int,
    val lastSyncAt: Long,
)

fun initialPlayerProgress(): PlayerProgress = PlayerProgress(
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
    coinValue = 0,
    journeyLevel = 0,
    lastSyncAt = System.currentTimeMillis(),
)

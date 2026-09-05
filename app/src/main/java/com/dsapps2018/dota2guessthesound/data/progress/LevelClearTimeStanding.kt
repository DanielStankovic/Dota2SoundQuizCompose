package com.dsapps2018.dota2guessthesound.data.progress

/**
 * One row on a Level Clear Time board. Wire DTOs stay inside [LevelClearTime].
 */
data class LevelClearTimeStanding(
    val standing: Long,
    val clearTimeMs: Long,
    val userName: String,
    val avatarUrl: String,
    val isCurrentUser: Boolean,
)

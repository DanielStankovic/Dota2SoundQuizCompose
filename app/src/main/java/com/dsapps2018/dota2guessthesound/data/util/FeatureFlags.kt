package com.dsapps2018.dota2guessthesound.data.util

/**
 * Runtime product gates that are easy to flip without hunting call sites.
 * Prefer these over deleting dormant features.
 */
object FeatureFlags {

    /**
     * Monthly arcade leaderboard (Home entry, Mode Result enqueue, Auth/Sync flush).
     * Off while there is no active leaderboard season.
     * Also gates Home FAQ (leaderboard-only help).
     */
    const val ARCADE_LEADERBOARD_ENABLED = false

    /**
     * Daily Joke carousel page and the KotL tease that jumps to it from Quiz / Fast Finger.
     */
    const val DAILY_JOKE_ENABLED = false
}

package com.dsapps2018.dota2guessthesound.data.util

/**
 * Runtime product gates that are easy to flip without hunting call sites.
 * Prefer these over deleting dormant features.
 */
object FeatureFlags {

    /**
     * Monthly arcade leaderboard (Home entry, Mode Result enqueue, Auth/Sync flush).
     * Off while there is no active leaderboard season.
     */
    const val ARCADE_LEADERBOARD_ENABLED = false
}

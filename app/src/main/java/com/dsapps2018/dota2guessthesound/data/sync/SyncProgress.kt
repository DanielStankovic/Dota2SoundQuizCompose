package com.dsapps2018.dota2guessthesound.data.sync

/**
 * Progress events from a Sync Session boot pipeline.
 */
sealed interface SyncProgress {
    data class Update(
        val progress: Float,
        val maxProgress: Float,
        val progressName: String,
    ) : SyncProgress

    data class Error(val message: String) : SyncProgress
    data object Finished : SyncProgress
    data object UpdateRequired : SyncProgress
}

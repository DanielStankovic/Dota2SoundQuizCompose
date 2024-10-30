package com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen

sealed interface ProgressUpdateEvent{
    data class ProgressUpdate(val progress: Float, val maxProgress:Float, val progressName: String) : ProgressUpdateEvent
    data class ProgressError(val error: String) : ProgressUpdateEvent
    data object SyncFinished : ProgressUpdateEvent
    data object ProgressUpdateRequired : ProgressUpdateEvent
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.board

fun formatLevelClearTime(ms: Long): String {
    val tenths = (ms / 100L).coerceAtLeast(0L)
    val minutes = tenths / 600L
    val seconds = (tenths % 600L) / 10L
    val tenth = tenths % 10L
    return if (minutes > 0L) {
        "%d:%02d.%d".format(minutes, seconds, tenth)
    } else {
        "%d.%ds".format(seconds, tenth)
    }
}

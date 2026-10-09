package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.board

/**
 * Level Clear Time display for the board and level picker (same string both places).
 * Whole seconds only (`0:45`, `1:05`). Ranking still uses full `clear_time_ms`
 * (then earlier `achieved_at`) server-side, so ties that look identical here stay ordered.
 */
fun formatLevelClearTime(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSec / 60L
    val seconds = totalSec % 60L
    return "%d:%02d".format(minutes, seconds)
}

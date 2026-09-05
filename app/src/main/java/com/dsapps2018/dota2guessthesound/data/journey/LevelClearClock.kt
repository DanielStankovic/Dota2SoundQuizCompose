package com.dsapps2018.dota2guessthesound.data.journey

import android.os.SystemClock

/**
 * Monotonic elapsed clock for Level Clear Time. Pauses with Journey Round Timer
 * (Extra Life Gate ad, overlay, ON_PAUSE). See ADR-0016.
 */
internal class LevelClearClock {
    private var startedAtElapsedRealtime: Long = 0L
    private var accumulatedPausedMs: Long = 0L
    private var pauseStartedAt: Long? = null
    private var running: Boolean = false

    fun start() {
        startedAtElapsedRealtime = SystemClock.elapsedRealtime()
        accumulatedPausedMs = 0L
        pauseStartedAt = null
        running = true
    }

    fun pause() {
        if (!running || pauseStartedAt != null) return
        pauseStartedAt = SystemClock.elapsedRealtime()
    }

    fun resume() {
        val started = pauseStartedAt ?: return
        accumulatedPausedMs += SystemClock.elapsedRealtime() - started
        pauseStartedAt = null
    }

    fun elapsedMs(): Long {
        if (!running) return 0L
        val now = SystemClock.elapsedRealtime()
        val paused = accumulatedPausedMs + (pauseStartedAt?.let { now - it } ?: 0L)
        return (now - startedAtElapsedRealtime - paused).coerceAtLeast(0L)
    }

    fun clear() {
        running = false
        pauseStartedAt = null
        accumulatedPausedMs = 0L
        startedAtElapsedRealtime = 0L
    }
}

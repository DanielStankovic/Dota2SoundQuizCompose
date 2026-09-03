package com.dsapps2018.dota2guessthesound.data.journey

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Internal Journey Round timer controller for Race Against Time and Soundquake intervals.
 * Owns tick loop, pause/resume, overlay blocking, and pending post-Gate restarts.
 */
internal class JourneyRoundTimer(
    private val onTick: (TimerState) -> Unit,
    private val onMarkPaused: () -> Unit,
    private val onTimeout: suspend (endsRoundOnTimeout: Boolean) -> Unit,
) {
    private var timerJob: Job? = null
    private var timerDurationMs: Long = 0L
    private var timerRemainingWhenPaused: Long = 0L
    private var isTimerPaused: Boolean = false

    var endsRoundOnTimeout: Boolean = false
    var extensionMs: Long = DEFAULT_TIMER_EXTENSION_SECONDS * 1000L
    /** Armed by time buyback or Soundquake-after-Gate; started only when surface is clear. */
    var pendingStartMs: Long? = null
    /** True while a fullscreen ad (or similar) covers the game; blocks lifecycle resume. */
    var blockedByOverlay: Boolean = false

    val durationMs: Long get() = timerDurationMs

    fun resetPolicy() {
        endsRoundOnTimeout = false
        extensionMs = DEFAULT_TIMER_EXTENSION_SECONDS * 1000L
        pendingStartMs = null
        blockedByOverlay = false
        clear()
    }

    fun start(durationMs: Long, scope: CoroutineScope) {
        Log.d(TAG, "start durationMs=$durationMs")
        timerDurationMs = durationMs
        isTimerPaused = false
        timerRemainingWhenPaused = 0L
        startCoroutine(scope)
    }

    fun pauseCapturingRemaining(remainingMs: Long) {
        if (timerJob?.isActive == true && !isTimerPaused) {
            isTimerPaused = true
            timerRemainingWhenPaused = remainingMs
            onMarkPaused()
            timerJob?.cancel()
        }
    }

    fun resume(scope: CoroutineScope) {
        if (isTimerPaused && timerRemainingWhenPaused > 0) {
            startCoroutine(scope)
        }
    }

    fun clear() {
        timerJob?.cancel()
        timerJob = null
        isTimerPaused = false
        timerRemainingWhenPaused = 0L
        timerDurationMs = 0L
        pendingStartMs = null
    }

    fun onSurfaceObscured(currentRemainingMs: Long) {
        blockedByOverlay = true
        pauseCapturingRemaining(currentRemainingMs)
    }

    /**
     * @return true if a pending start was consumed (caller should not also resume).
     */
    fun onSurfaceClear(scope: CoroutineScope?, showContinueDialog: Boolean): Boolean {
        blockedByOverlay = false
        if (showContinueDialog) return false
        pendingStartMs?.let { ms ->
            pendingStartMs = null
            val s = scope ?: return false
            start(ms, s)
            return true
        }
        scope?.let { resume(it) }
        return false
    }

    fun onHostPaused(currentRemainingMs: Long) {
        pauseCapturingRemaining(currentRemainingMs)
    }

    fun onHostResumed(scope: CoroutineScope?, showContinueDialog: Boolean) {
        if (blockedByOverlay) return
        if (showContinueDialog) return
        // Do not start pending time buyback here — that waits for ad dismiss via onSurfaceClear.
        scope?.let { resume(it) }
    }

    private fun startCoroutine(scope: CoroutineScope) {
        timerJob?.cancel()
        timerJob = scope.launch {
            Log.d(TAG, "startCoroutine isTimerPaused=$isTimerPaused")

            val startTime = if (isTimerPaused) {
                isTimerPaused = false
                System.currentTimeMillis() - (timerDurationMs - timerRemainingWhenPaused)
            } else {
                System.currentTimeMillis()
            }

            val endTime =
                startTime + (if (isTimerPaused) timerRemainingWhenPaused else timerDurationMs)

            while (System.currentTimeMillis() < endTime && !isTimerPaused) {
                val remaining = endTime - System.currentTimeMillis()
                onTick(
                    TimerState(
                        remainingMs = remaining,
                        totalMs = timerDurationMs,
                        isWarning = remaining < timerDurationMs / 4,
                        isPaused = false
                    )
                )
                delay(100)
            }

            if (!isTimerPaused) {
                onTimeout(endsRoundOnTimeout)
            }
        }
    }

    companion object {
        private const val TAG = "JourneyRoundTimer"
        const val DEFAULT_TIMER_EXTENSION_SECONDS = 20
    }
}

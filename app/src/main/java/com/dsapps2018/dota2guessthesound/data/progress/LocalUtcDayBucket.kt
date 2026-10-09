package com.dsapps2018.dota2guessthesound.data.progress

import android.content.SharedPreferences
import android.os.SystemClock
import com.dsapps2018.dota2guessthesound.data.util.Constants
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared UTC-day bucket for local daily caps, hardened against casual clock changes.
 *
 * A calendar-day rollover is accepted only when wall-clock UTC date changes **and**
 * [SystemClock.elapsedRealtime] (monotonic while the device is up) advanced roughly
 * in line with that jump. Setting the date forward in Settings without waiting, or
 * rolling the clock back, does not clear counts.
 *
 * Not cheat-proof: power-off/reboot + long waits, prefs wipe, or a rooted device can
 * still bypass. Server-side caps are the real fix if this needs to be hard.
 */
@Singleton
class LocalUtcDayBucket @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    /**
     * Sync the stored UTC day for [dayKeyPref]. When a legitimate new day is accepted,
     * resets [countKeyPref] to 0.
     */
    fun syncDay(
        dayKeyPref: String,
        countKeyPref: String,
        wallNowMs: Long = System.currentTimeMillis(),
        elapsedNowMs: Long = SystemClock.elapsedRealtime(),
    ) {
        val wallDay = utcDayKey(wallNowMs)
        val storedDay = sharedPreferences.getString(dayKeyPref, null)
        val lastWall = sharedPreferences.getLong(Constants.PREF_LOCAL_CAP_ANCHOR_WALL_MS, 0L)
        val lastElapsed = sharedPreferences.getLong(Constants.PREF_LOCAL_CAP_ANCHOR_ELAPSED_MS, 0L)

        val acceptNewDay = when {
            storedDay == null -> true
            storedDay == wallDay -> false
            else -> canAcceptDayChange(lastWall, lastElapsed, wallNowMs, elapsedNowMs)
        }

        // Always advance monotonic uptime. Only advance wall anchor when the day is
        // trusted (same day or accepted rollover) so a rejected forward jump cannot
        // freeze lastWall in the future.
        val editor = sharedPreferences.edit()
            .putLong(Constants.PREF_LOCAL_CAP_ANCHOR_ELAPSED_MS, elapsedNowMs)

        when {
            acceptNewDay -> {
                editor.putString(dayKeyPref, wallDay)
                editor.putLong(Constants.PREF_LOCAL_CAP_ANCHOR_WALL_MS, wallNowMs)
                if (storedDay != null && storedDay != wallDay) {
                    editor.putInt(countKeyPref, 0)
                }
            }
            storedDay == wallDay -> {
                editor.putLong(Constants.PREF_LOCAL_CAP_ANCHOR_WALL_MS, wallNowMs)
            }
            // rejected rollover: keep frozen day key + previous wall anchor
        }

        editor.apply()
    }

    /** Cooldown remaining using monotonic uptime — immune to wall-clock jumps. */
    fun cooldownRemainingMs(
        lastElapsedPref: String,
        cooldownMs: Long,
        elapsedNowMs: Long = SystemClock.elapsedRealtime(),
    ): Long {
        val last = sharedPreferences.getLong(lastElapsedPref, 0L)
        if (last <= 0L) return 0L
        // After reboot elapsedRealtime restarts below the stored stamp.
        if (elapsedNowMs < last) return 0L
        return (cooldownMs - (elapsedNowMs - last)).coerceAtLeast(0L)
    }

    fun markCooldownStart(
        lastElapsedPref: String,
        elapsedNowMs: Long = SystemClock.elapsedRealtime(),
    ) {
        sharedPreferences.edit().putLong(lastElapsedPref, elapsedNowMs).apply()
    }

    private fun canAcceptDayChange(
        lastWallMs: Long,
        lastElapsedMs: Long,
        wallNowMs: Long,
        elapsedNowMs: Long,
    ): Boolean {
        if (lastWallMs <= 0L || lastElapsedMs <= 0L) return true

        val wallDelta = wallNowMs - lastWallMs
        // Clock rolled back — do not grant a fresh day.
        if (wallDelta < -ROLLBACK_SLACK_MS) return false

        // Reboot: elapsedRealtime restarted. Require real uptime since boot before
        // honoring a wall-day change (blocks instant date-set + reboot).
        val rebooted = elapsedNowMs + REBOOT_DETECT_SLACK_MS < lastElapsedMs
        if (rebooted) {
            return elapsedNowMs >= MIN_UPTIME_AFTER_REBOOT_MS
        }

        val elapsedDelta = (elapsedNowMs - lastElapsedMs).coerceAtLeast(0L)
        // Wall jumped ahead of real uptime (date set forward) → reject until enough
        // real uptime accumulates. Overnight OK: elapsed tracks sleep.
        return elapsedDelta >= MIN_UPTIME_FOR_DAY_ROLL_MS ||
            elapsedDelta + WALL_ELAPSED_SLACK_MS >= wallDelta
    }

    private fun utcDayKey(wallMs: Long): String =
        LocalDate.ofInstant(Instant.ofEpochMilli(wallMs), ZoneOffset.UTC).toString()

    private companion object {
        const val ROLLBACK_SLACK_MS = 5 * 60 * 1000L
        const val REBOOT_DETECT_SLACK_MS = 5 * 1000L
        const val MIN_UPTIME_AFTER_REBOOT_MS = 6 * 60 * 60 * 1000L
        const val MIN_UPTIME_FOR_DAY_ROLL_MS = 6 * 60 * 60 * 1000L
        const val WALL_ELAPSED_SLACK_MS = 2 * 60 * 60 * 1000L
    }
}

/** Milliseconds until the next UTC midnight (wall clock). */
fun msUntilNextUtcMidnight(wallNowMs: Long = System.currentTimeMillis()): Long {
    val now = Instant.ofEpochMilli(wallNowMs).atZone(ZoneOffset.UTC)
    val next = now.toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
    return (next.toEpochMilli() - wallNowMs).coerceAtLeast(0L)
}

package com.dsapps2018.dota2guessthesound.data.progress

import android.content.SharedPreferences
import com.dsapps2018.dota2guessthesound.data.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

data class SoundSamplerAdUnlockStatus(
    val remainingToday: Int,
    val canUnlock: Boolean,
)

/**
 * Rate-limited Sound Sampler ad unlocks. Local anti-farm only — not synced.
 * Daily cap uses UTC day via [LocalUtcDayBucket] (same clock-skew rules as [CoinAdGrant]).
 * Pref keys keep legacy `primer_ad_unlock_*` values so in-flight daily counts survive the rename.
 * See docs/adr/0022-journey-assists.md.
 */
@Singleton
class SoundSamplerAdUnlock @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val localUtcDayBucket: LocalUtcDayBucket,
) {

    fun status(): SoundSamplerAdUnlockStatus {
        localUtcDayBucket.syncDay(
            dayKeyPref = Constants.PREF_SOUND_SAMPLER_AD_UNLOCK_DAY_KEY,
            countKeyPref = Constants.PREF_SOUND_SAMPLER_AD_UNLOCK_DAY_COUNT,
        )
        val remaining =
            (Constants.SOUND_SAMPLER_AD_UNLOCK_DAILY_CAP - unlocksToday()).coerceAtLeast(0)
        return SoundSamplerAdUnlockStatus(
            remainingToday = remaining,
            canUnlock = remaining > 0,
        )
    }

    /**
     * Record one Sound Sampler unlock after a rewarded ad completed.
     * Returns false if the daily cap blocks it (caller should not have shown the ad).
     */
    fun recordUnlock(): Boolean {
        val before = status()
        if (!before.canUnlock) return false
        sharedPreferences.edit()
            .putInt(Constants.PREF_SOUND_SAMPLER_AD_UNLOCK_DAY_COUNT, unlocksToday() + 1)
            .apply()
        return true
    }

    private fun unlocksToday(): Int =
        sharedPreferences.getInt(Constants.PREF_SOUND_SAMPLER_AD_UNLOCK_DAY_COUNT, 0)
}

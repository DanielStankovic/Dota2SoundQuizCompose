package com.dsapps2018.dota2guessthesound.data.progress

import android.content.SharedPreferences
import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.dsapps2018.dota2guessthesound.data.util.Constants
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

data class CoinAdGrantStatus(
    val grantAmount: Int = Constants.COIN_AD_GRANT_AMOUNT,
    val remainingToday: Int,
    val cooldownRemainingMs: Long,
    val canGrant: Boolean,
)

/**
 * Rate-limited rewarded-ad coin faucet (Assist tray + Home coin-info).
 * Local anti-farm only — not synced. See docs/adr/0022-journey-assists.md.
 */
@Singleton
class CoinAdGrant @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val playerProgressRepository: PlayerProgressRepository,
    private val productAnalytics: ProductAnalytics,
) {

    fun status(nowMs: Long = System.currentTimeMillis()): CoinAdGrantStatus {
        pruneDayIfNeeded(nowMs)
        val remaining = (Constants.COIN_AD_GRANT_DAILY_CAP - grantsToday()).coerceAtLeast(0)
        val cooldownMs = cooldownRemainingMs(nowMs)
        return CoinAdGrantStatus(
            remainingToday = remaining,
            cooldownRemainingMs = cooldownMs,
            canGrant = remaining > 0 && cooldownMs <= 0L,
        )
    }

    /**
     * Apply grant after a rewarded ad completed. Returns false if blocked by
     * cooldown / daily cap (caller should not have shown the ad).
     */
    suspend fun grantFromAd(nowMs: Long = System.currentTimeMillis()): Boolean {
        val before = status(nowMs)
        if (!before.canGrant) return false
        playerProgressRepository.adjustCoins(Constants.COIN_AD_GRANT_AMOUNT)
        productAnalytics.earnCoins(
            Constants.COIN_AD_GRANT_AMOUNT.toLong(),
            ProductAnalytics.SOURCE_COIN_AD_GRANT,
        )
        sharedPreferences.edit()
            .putLong(Constants.PREF_COIN_AD_GRANT_LAST_MS, nowMs)
            .putInt(Constants.PREF_COIN_AD_GRANT_DAY_COUNT, grantsToday() + 1)
            .apply()
        return true
    }

    private fun cooldownRemainingMs(nowMs: Long): Long {
        val last = sharedPreferences.getLong(Constants.PREF_COIN_AD_GRANT_LAST_MS, 0L)
        if (last <= 0L) return 0L
        val elapsed = nowMs - last
        return (Constants.COIN_AD_GRANT_COOLDOWN_MS - elapsed).coerceAtLeast(0L)
    }

    private fun grantsToday(): Int =
        sharedPreferences.getInt(Constants.PREF_COIN_AD_GRANT_DAY_COUNT, 0)

    private fun pruneDayIfNeeded(nowMs: Long) {
        val today = LocalDate.ofInstant(Instant.ofEpochMilli(nowMs), ZoneOffset.UTC).toString()
        val stored = sharedPreferences.getString(Constants.PREF_COIN_AD_GRANT_DAY_KEY, null)
        if (stored == today) return
        sharedPreferences.edit()
            .putString(Constants.PREF_COIN_AD_GRANT_DAY_KEY, today)
            .putInt(Constants.PREF_COIN_AD_GRANT_DAY_COUNT, 0)
            .apply()
    }
}

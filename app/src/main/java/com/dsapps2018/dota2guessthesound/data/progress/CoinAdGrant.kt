package com.dsapps2018.dota2guessthesound.data.progress

import android.content.SharedPreferences
import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.dsapps2018.dota2guessthesound.data.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

data class CoinAdGrantStatus(
    val grantAmount: Int = Constants.COIN_AD_GRANT_AMOUNT,
    val remainingToday: Int,
    val cooldownRemainingMs: Long,
    val canGrant: Boolean,
)

/**
 * Rate-limited rewarded-ad coin faucet (Assist tray + Home Invoker).
 * Local anti-farm only — not synced. Daily cap uses UTC day via [LocalUtcDayBucket]
 * (resists casual device date changes). Cooldown uses monotonic uptime.
 * See docs/adr/0022-journey-assists.md.
 */
@Singleton
class CoinAdGrant @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val playerProgressRepository: PlayerProgressRepository,
    private val productAnalytics: ProductAnalytics,
    private val localUtcDayBucket: LocalUtcDayBucket,
) {

    fun status(): CoinAdGrantStatus {
        localUtcDayBucket.syncDay(
            dayKeyPref = Constants.PREF_COIN_AD_GRANT_DAY_KEY,
            countKeyPref = Constants.PREF_COIN_AD_GRANT_DAY_COUNT,
        )
        val remaining = (Constants.COIN_AD_GRANT_DAILY_CAP - grantsToday()).coerceAtLeast(0)
        val cooldownMs = localUtcDayBucket.cooldownRemainingMs(
            lastElapsedPref = Constants.PREF_COIN_AD_GRANT_LAST_ELAPSED_MS,
            cooldownMs = Constants.COIN_AD_GRANT_COOLDOWN_MS,
        )
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
    suspend fun grantFromAd(): Boolean {
        val before = status()
        if (!before.canGrant) return false
        playerProgressRepository.adjustCoins(Constants.COIN_AD_GRANT_AMOUNT)
        productAnalytics.earnCoins(
            Constants.COIN_AD_GRANT_AMOUNT.toLong(),
            ProductAnalytics.SOURCE_COIN_AD_GRANT,
        )
        localUtcDayBucket.markCooldownStart(Constants.PREF_COIN_AD_GRANT_LAST_ELAPSED_MS)
        sharedPreferences.edit()
            .putInt(Constants.PREF_COIN_AD_GRANT_DAY_COUNT, grantsToday() + 1)
            .apply()
        return true
    }

    private fun grantsToday(): Int =
        sharedPreferences.getInt(Constants.PREF_COIN_AD_GRANT_DAY_COUNT, 0)
}

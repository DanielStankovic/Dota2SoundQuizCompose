package com.dsapps2018.dota2guessthesound.data.invoker

import com.dsapps2018.dota2guessthesound.data.analytics.AnalyticsMode
import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Deep in-process module for Invoker entry (paid mode start and matching ad grant).
 * See docs/adr/0009-invoker-entry.md and ADR-0001 for sync-on-spend.
 */
class InvokerEntry @Inject constructor(
    private val playerProgressRepository: PlayerProgressRepository,
    private val productAnalytics: ProductAnalytics,
) {

    fun canEnter(coinValue: Int): Boolean = coinValue >= COIN_COST

    /** Spends [COIN_COST] via Player Progress (immediate sync when signed in). */
    suspend fun enter(): Boolean {
        val coins = playerProgressRepository.progress.first().coinValue
        if (!canEnter(coins)) return false
        playerProgressRepository.adjustCoins(-COIN_COST)
        productAnalytics.spendCoins(
            COIN_COST.toLong(),
            ProductAnalytics.ITEM_INVOKER_ENTRY,
        )
        productAnalytics.selectMode(AnalyticsMode.Invoker)
        return true
    }

    /** Grants [COIN_COST] after a rewarded ad (same adjustCoins sync policy). */
    suspend fun grantFromAd() {
        playerProgressRepository.adjustCoins(COIN_COST)
        productAnalytics.earnCoins(
            COIN_COST.toLong(),
            ProductAnalytics.SOURCE_INVOKER_AD_GRANT,
        )
    }

    companion object {
        const val COIN_COST = 200
    }
}

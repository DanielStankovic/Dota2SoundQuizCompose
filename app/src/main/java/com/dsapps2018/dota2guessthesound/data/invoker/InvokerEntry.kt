package com.dsapps2018.dota2guessthesound.data.invoker

import com.dsapps2018.dota2guessthesound.data.analytics.AnalyticsMode
import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Deep in-process module for Invoker entry (paid mode start).
 * Coin Ad Grant for broke players is the shared faucet ([com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrant]),
 * not a matching +cost Invoker grant. See docs/adr/0009-invoker-entry.md and ADR-0001.
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

    companion object {
        const val COIN_COST = 40
    }
}

package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrantStatus
import com.dsapps2018.dota2guessthesound.data.util.Constants

enum class AssistDisableReason {
    AmongHeroes,
    UnknownCount,
}

/**
 * Round-facing Journey Assist chrome for the Assist tray and portrait badges.
 */
data class JourneyAssistUiState(
    val countUnlocked: Boolean = false,
    val primerUnlocked: Boolean = false,
    val countDisabledReason: AssistDisableReason? = null,
    val primerDisabledReason: AssistDisableReason? = null,
    /** Correct-sound counts for visible portrait hero ids (when Count unlocked). */
    val soundCountsByHeroId: Map<Int, Int> = emptyMap(),
    val coinBalance: Int = 0,
    val countCost: Int = Constants.SOUND_COUNT_REVEAL_COST,
    val coinAdGrant: CoinAdGrantStatus = CoinAdGrantStatus(
        remainingToday = Constants.COIN_AD_GRANT_DAILY_CAP,
        cooldownRemainingMs = 0L,
        canGrant = true,
    ),
    /** Open Primer overlay for this hero id, or null. */
    val primerHeroId: Int? = null,
    val primerHeroName: String? = null,
    val primerSounds: List<SoundModel> = emptyList(),
    /** When true, Assist tray should open and highlight Primer CTA. */
    val requestOpenTrayForPrimer: Boolean = false,
) {
    val countPurchaseAvailable: Boolean
        get() = countDisabledReason == null && !countUnlocked

    val primerPurchaseAvailable: Boolean
        get() = primerDisabledReason == null && !primerUnlocked

    val canAffordCount: Boolean
        get() = coinBalance >= countCost
}

internal object JourneyAssistPolicy {

    fun disableReasons(affixes: List<AffixModel>): Pair<AssistDisableReason?, AssistDisableReason?> {
        val keys = affixes.map { it.affix.lowercase().replace(" ", "_") }.toSet()
        val amongHeroes = "among_heroes" in keys
        val unknownCount = "unknown_count" in keys
        val countReason = when {
            amongHeroes -> AssistDisableReason.AmongHeroes
            unknownCount -> AssistDisableReason.UnknownCount
            else -> null
        }
        val primerReason = when {
            amongHeroes -> AssistDisableReason.AmongHeroes
            else -> null
        }
        return countReason to primerReason
    }
}

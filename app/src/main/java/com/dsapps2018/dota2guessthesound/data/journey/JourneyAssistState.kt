package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrantStatus
import com.dsapps2018.dota2guessthesound.data.progress.SoundSamplerAdUnlockStatus
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
    val soundSamplerUnlocked: Boolean = false,
    val countDisabledReason: AssistDisableReason? = null,
    val soundSamplerDisabledReason: AssistDisableReason? = null,
    /** Correct-sound counts for visible portrait hero ids (when Count unlocked). */
    val soundCountsByHeroId: Map<Int, Int> = emptyMap(),
    val coinBalance: Int = 0,
    val countCost: Int = Constants.SOUND_COUNT_REVEAL_COST,
    val coinAdGrant: CoinAdGrantStatus = CoinAdGrantStatus(
        remainingToday = Constants.COIN_AD_GRANT_DAILY_CAP,
        cooldownRemainingMs = 0L,
        canGrant = true,
    ),
    val soundSamplerAdUnlock: SoundSamplerAdUnlockStatus = SoundSamplerAdUnlockStatus(
        remainingToday = Constants.SOUND_SAMPLER_AD_UNLOCK_DAILY_CAP,
        canUnlock = true,
    ),
    /** Open Sound Sampler overlay for this hero id, or null. */
    val soundSamplerHeroId: Int? = null,
    val soundSamplerHeroName: String? = null,
    val soundSamplerSounds: List<SoundModel> = emptyList(),
    /** When true, Assist tray should open and highlight Sound Sampler CTA. */
    val requestOpenTrayForSoundSampler: Boolean = false,
) {
    val countPurchaseAvailable: Boolean
        get() = countDisabledReason == null && !countUnlocked

    val soundSamplerPurchaseAvailable: Boolean
        get() = soundSamplerDisabledReason == null &&
            !soundSamplerUnlocked &&
            soundSamplerAdUnlock.canUnlock

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
        val soundSamplerReason = when {
            amongHeroes -> AssistDisableReason.AmongHeroes
            else -> null
        }
        return countReason to soundSamplerReason
    }
}

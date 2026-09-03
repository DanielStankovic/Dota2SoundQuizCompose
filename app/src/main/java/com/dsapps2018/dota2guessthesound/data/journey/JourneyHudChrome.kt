package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState

/**
 * Journey Round → UI chrome for the Affix status band.
 * AffixUIState stays AffixEngine-internal; portrait blur lives on [HeroPortraitSlot].
 */
data class JourneyHudChrome(
    val showHearts: Boolean = true,
    val showSoundCounter: Boolean = true,
    val showMarkedSoundCounter: Boolean = true,
    val showTimer: Boolean = false,
)

internal fun AffixUIState.toHudChrome(): JourneyHudChrome =
    JourneyHudChrome(
        showHearts = showHearts,
        showSoundCounter = showSoundCounter,
        showMarkedSoundCounter = showMarkedSoundCounter,
        showTimer = showTimer,
    )

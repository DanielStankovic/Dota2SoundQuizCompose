package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

/**
 * Journey level list look-ahead for Affix identity. See CONTEXT.md (**Affix fog**).
 */
object AffixFog {
    const val LOOKAHEAD_LOCKED_LEVELS = 3

    fun revealsIdentity(level: Int, journeyLevel: Int): Boolean =
        level <= journeyLevel + 1 + LOOKAHEAD_LOCKED_LEVELS
}

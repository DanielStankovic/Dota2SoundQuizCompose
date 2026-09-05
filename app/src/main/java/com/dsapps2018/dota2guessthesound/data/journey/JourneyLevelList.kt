package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.model.AffixModel

/** Unlock state of a Journey level list row, from journey level. */
enum class JourneyLevelAccess {
    Cleared,
    Playable,
    Locked,
}

/** Affix identity on a list card: revealed or one mystery token (Affix fog). */
sealed interface AffixListSlot {
    data class Revealed(val affix: AffixModel) : AffixListSlot
    data object Mystery : AffixListSlot
}

/**
 * Journey level list row after Affix→presentation mapping (unlock + Affix fog).
 * Fogged cards never carry catalog Affix identity.
 */
data class JourneyLevelListRow(
    val id: Int,
    val level: Int,
    val access: JourneyLevelAccess,
    val affixSlots: List<AffixListSlot>,
)

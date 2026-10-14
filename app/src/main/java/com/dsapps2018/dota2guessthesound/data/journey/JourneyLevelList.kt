package com.dsapps2018.dota2guessthesound.data.journey

import com.dsapps2018.dota2guessthesound.data.model.AffixModel

/** Unlock state of a Journey level list row, from journey level. */
enum class JourneyLevelAccess {
    Cleared,
    Playable,
    Locked,
}

/** Affix identity on a list card (always revealed). */
sealed interface AffixListSlot {
    data class Revealed(val affix: AffixModel) : AffixListSlot
    data object Mystery : AffixListSlot
}

/** Journey level list row after unlock + Affix→presentation mapping. */
data class JourneyLevelListRow(
    val id: Int,
    val level: Int,
    val access: JourneyLevelAccess,
    val affixSlots: List<AffixListSlot>,
)

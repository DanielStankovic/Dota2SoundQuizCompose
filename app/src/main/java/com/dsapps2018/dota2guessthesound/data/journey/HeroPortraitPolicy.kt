package com.dsapps2018.dota2guessthesound.data.journey

import android.content.res.Resources
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState

/**
 * One Hero slot in the Journey portrait row after Affix + level membership resolve.
 */
data class HeroPortraitSlot(
    val imageResId: Int,
    /** Compose blur radius in dp; `0` means no blur. */
    val blurRadiusDp: Float = 0f,
)

/**
 * Radiant / Dire portrait rows for a Journey Round.
 * Dire may be unused by UI today; same policy applies when the Dire row ships.
 */
data class HeroPortraitRows(
    val radiant: List<HeroPortraitSlot>,
    val dire: List<HeroPortraitSlot>,
)

/**
 * Deepens Hero portrait Affix resolution: Affix enable flags + level-authored
 * membership + caster names → portrait slots. Internal to Journey Round.
 */
internal object HeroPortraitPolicy {

    fun resolve(
        affixUI: AffixUIState,
        radiantHeroIds: List<Int>,
        direHeroIds: List<Int>,
        maskedHeroIdsFromLevel: List<Int>,
        blurredHeroIdsFromLevel: List<Int>,
        hiddenHeroId: Int?,
        casterNameById: Map<Int, String>,
        resources: Resources,
        packageName: String,
    ): HeroPortraitRows {
        val heroIds = radiantHeroIds + direHeroIds
        val maskedHeroIds = when {
            affixUI.useQuestionMarkHeroPortraits -> heroIds.toSet()
            affixUI.usePartialVeil -> maskedHeroIdsFromLevel.toSet()
            else -> emptySet()
        }
        // Among Heroes: omit authored id from portraits only; sounds still use full heroIds.
        // Soft guard: refuse only when omit would empty the currently shown Radiant row.
        val omittedHeroId = hiddenHeroId?.takeIf { id ->
            if (!affixUI.useAmongHeroes || id !in heroIds) return@takeIf false
            val wouldEmptyRadiantRow =
                id in radiantHeroIds && radiantHeroIds.none { it != id }
            !wouldEmptyRadiantRow
        }
        val radiantPortraitHeroes = radiantHeroIds.filter { it != omittedHeroId }
        val direPortraitHeroes = direHeroIds.filter { it != omittedHeroId }
        val blurredHeroIds =
            if (affixUI.blurHeroImages) blurredHeroIdsFromLevel.toSet() else emptySet()
        val blurRadius = if (affixUI.blurHeroImages) affixUI.blurIntensity else 0f

        fun slotFor(heroId: Int, radiantNaming: Boolean): HeroPortraitSlot {
            val imageResId = if (heroId in maskedHeroIds) {
                R.drawable.hero_question_mark
            } else {
                val name = casterNameById[heroId].orEmpty()
                val drawableName = if (radiantNaming) {
                    "hero_${name.lowercase().replace("'s", "s").replace("-", "")}"
                } else {
                    name.replace("'s", "s").replace("-", "")
                }
                resources.getIdentifier(drawableName, "drawable", packageName)
            }
            val blur = if (heroId in blurredHeroIds && heroId !in maskedHeroIds) {
                blurRadius
            } else {
                0f
            }
            return HeroPortraitSlot(imageResId = imageResId, blurRadiusDp = blur)
        }

        return HeroPortraitRows(
            radiant = radiantPortraitHeroes.map { slotFor(it, radiantNaming = true) },
            dire = direPortraitHeroes.map { slotFor(it, radiantNaming = false) },
        )
    }
}

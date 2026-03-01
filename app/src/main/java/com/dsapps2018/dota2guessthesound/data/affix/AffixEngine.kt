package com.dsapps2018.dota2guessthesound.data.affix

import com.dsapps2018.dota2guessthesound.data.affix.strategies.BlurredVisionAffixStrategy
import com.dsapps2018.dota2guessthesound.data.affix.strategies.HiddenHeroAffixStrategy
import com.dsapps2018.dota2guessthesound.data.affix.strategies.HiddenMarksAffixStrategy
import com.dsapps2018.dota2guessthesound.data.affix.strategies.RaceAgainstTimeAffixStrategy
import com.dsapps2018.dota2guessthesound.data.affix.strategies.UnknownCountAffixStrategy
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * Central engine that manages and applies all active affixes to the game state
 */
class AffixEngine(private val activeAffixes: List<AffixModel>) {

    private val affixStrategies: List<AffixStrategy> = createAffixStrategies()

    private fun createAffixStrategies(): List<AffixStrategy> {
        return activeAffixes.mapNotNull { affix ->
            when (affix.affix.lowercase().replace(" ", "_")) {
                "hidden_marks" -> HiddenMarksAffixStrategy()
                "blurred_vision", "blurred_vision_2" -> BlurredVisionAffixStrategy(
                    getValueFromData(
                        affix,
                        "blur"
                    )
                )
                "race_against_time" -> RaceAgainstTimeAffixStrategy(
                    getValueFromData(
                        affix,
                        "timer"
                    )
                )

                "echo_limit" -> EchoLimitAffixStrategy()
                "mirror_mode" -> MirrorModeAffixStrategy()
                "the_hidden_hero" -> HiddenHeroAffixStrategy()
                "unknown_count" -> UnknownCountAffixStrategy()
                "fragile_spirit" -> FragileSpiritAffixStrategy()
                "sudden_death" -> SuddenDeathAffixStrategy()
                "soundquake" -> SoundquakeAffixStrategy()
                "among_heroes" -> AmongHeroesAffixStrategy()
                else -> null // Unknown affix, skip
            }
        }
    }

    /**
     * Apply all UI-related affix modifications
     */
    fun applyUIModifications(): AffixUIState {
        var uiState = AffixUIState()

        affixStrategies.forEach { strategy ->
            uiState = strategy.modifyUI(uiState)
        }

        return uiState
    }

    /**
     * Apply all gameplay-related affix modifications
     */
    fun applyGameplayModifications(gameState: AffixGameState): AffixGameState {
        var modifiedState = gameState

        affixStrategies.forEach { strategy ->
            modifiedState = strategy.modifyGameplay(modifiedState)
        }

        return modifiedState
    }

    /**
     * Check if answer submission should be modified by affixes
     */
    fun modifyAnswerValidation(
        selectedSounds: Set<Int>,
        correctSounds: Set<Int>
    ): AnswerValidationResult {
        var result = AnswerValidationResult(
            isCorrect = selectedSounds.size == correctSounds.size && correctSounds.containsAll(
                selectedSounds
            ),
            originalLogic = true
        )

        affixStrategies.forEach { strategy ->
            result = strategy.modifyAnswerValidation(selectedSounds, correctSounds, result)
        }

        return result
    }

    /**
     * Get any timer-related modifications
     */
    fun getTimerConfiguration(): TimerConfiguration? {
        affixStrategies.forEach { strategy ->
            strategy.getTimerConfiguration()?.let { return it }
        }
        return null
    }

    /**
     * Get sound play limitations
     */
    fun getSoundLimitations(): SoundLimitations? {
        affixStrategies.forEach { strategy ->
            strategy.getSoundLimitations()?.let { return it }
        }
        return null
    }

    fun getValueFromData(affix: AffixModel, key: String): String? {
        val jsonObject = affix.data

        // Get the element by key, if it exists
        val value = jsonObject[key]

        // Convert it to a string (handle different possible types)
        return when (value) {
            is JsonPrimitive -> value.contentOrNull
            else -> value?.toString()
        }
    }
}
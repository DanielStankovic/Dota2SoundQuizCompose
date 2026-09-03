package com.dsapps2018.dota2guessthesound.data.affix

/**
 * Journey level fields Affix strategies need to resolve budgets / timers.
 * Portrait membership stays on Hero Portrait Policy (not AffixStrategy).
 */
data class AffixLevelContext(
    val maxSounds: Int,
    val echoLimitOffset: Int?,
    val timerSeconds: Int?,
    val timerExtensionSeconds: Int?,
)

/**
 * Resolved Race / Soundquake timer after Affix + level fields join.
 */
data class ResolvedAffixTimer(
    val durationMs: Long,
    val endsRoundOnTimeout: Boolean,
    val extensionMs: Long,
)

/**
 * Base interface for all affix strategies
 */
interface AffixStrategy {

    /**
     * Modify UI elements based on this affix
     */
    fun modifyUI(currentState: AffixUIState): AffixUIState = currentState

    /**
     * Modify gameplay mechanics based on this affix
     */
    fun modifyGameplay(currentState: AffixGameState): AffixGameState = currentState

    /**
     * Modify answer validation logic.
     * [allSoundIds] is the full board (needed by Mirror Mode and similar).
     */
    fun modifyAnswerValidation(
        selectedSounds: Set<Int>,
        correctSounds: Set<Int>,
        allSoundIds: Set<Int>,
        currentResult: AnswerValidationResult
    ): AnswerValidationResult = currentResult

    /**
     * Provide timer configuration if this affix adds a timer.
     * Duration may be overridden by [AffixLevelContext.timerSeconds] in AffixEngine.
     */
    fun getTimerConfiguration(): TimerConfiguration? = null

    /**
     * Echo Limit and similar: resolved play budget from level fields, or null if inactive.
     */
    fun resolveSoundPlayBudget(level: AffixLevelContext): Int? = null
}

/**
 * Data classes for affix state management
 */
data class AffixUIState(
    val showSoundCounter: Boolean = true,
    val showMarkedSoundCounter: Boolean = true,
    /**
     * Hidden Hero: keep the hero-row Image layout, but load the `?` drawable
     * in each portrait slot instead of the real hero art.
     */
    val useQuestionMarkHeroPortraits: Boolean = false,
    /**
     * Partial Veil: keep the hero-row layout; Journey Round masks only
     * level-authored `masked_hero_ids` with the `?` drawable.
     */
    val usePartialVeil: Boolean = false,
    /**
     * Among Heroes: omit level-authored `hidden_hero_id` from the portrait row
     * (no `?`); that hero’s sounds stay on the board.
     */
    val useAmongHeroes: Boolean = false,
    /**
     * Blurred Vision / Blurred Vision 2: Affix is active. Which slots blur comes from
     * level-authored `blurred_hero_ids` (resolved in Journey Round); never blur `?` slots.
     */
    val blurHeroImages: Boolean = false,
    val blurIntensity: Float = 0f,
    val showHearts: Boolean = true,
    val showTimer: Boolean = false
)

data class AffixGameState(
    val modifiedHeartCount: Int? = null,
    /**
     * When false, Journey Round skips the Extra Life Gate after the last heart
     * (Sudden Death). Fragile Spirit leaves this true.
     */
    val extraLifeGateAllowed: Boolean = true,
    /**
     * Soundquake family: clear all marks when the quake interval fires
     * (Aftershock / Cataclysm). Soundquake / Fracture leave marks in place.
     */
    val soundquakeClearsMarks: Boolean = false,
    /**
     * Soundquake family: each quake also costs one heart
     * (Fracture / Cataclysm). Uses the same Extra Life Gate as a wrong submit.
     */
    val soundquakeDrainsHeart: Boolean = false,
)

data class AnswerValidationResult(
    val isCorrect: Boolean,
    val originalLogic: Boolean = true,
    val customMessage: String? = null
)

data class TimerConfiguration(
    val durationMs: Long,
    val showWarningAt: Long = durationMs / 4, // Show warning at 25% remaining
    /**
     * Race Against Time: true — timeout ends the round (Extra Life Gate may offer +time).
     * Soundquake family: false — timeout reshuffles (and may clear marks / drain a heart).
     */
    val endsRoundOnTimeout: Boolean = true,
)

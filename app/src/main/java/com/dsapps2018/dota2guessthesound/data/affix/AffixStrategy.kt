package com.dsapps2018.dota2guessthesound.data.affix

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
     * Provide timer configuration if this affix adds a timer
     */
    fun getTimerConfiguration(): TimerConfiguration? = null
    
    /**
     * Provide sound limitations if this affix limits sound plays
     */
    fun getSoundLimitations(): SoundLimitations? = null
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
    val maxSoundPlays: Int? = null,
    val currentSoundPlays: Int = 0,
    val timerDurationMs: Long? = null,
    val invertAnswerLogic: Boolean = false,
    val modifiedHeartCount: Int? = null,
    /**
     * When false, Journey Round skips the Extra Life Gate after the last heart
     * (Sudden Death). Fragile Spirit leaves this true.
     */
    val extraLifeGateAllowed: Boolean = true,
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
     * Soundquake (later): false — timeout reshuffles instead.
     */
    val endsRoundOnTimeout: Boolean = true,
)

/**
 * Echo Limit is active. Play budget is authored on the Journey level
 * (`max_sounds` + `echo_limit_offset`), not on Affix `data`.
 */
data class SoundLimitations(
    val warningMessage: String? = null
)

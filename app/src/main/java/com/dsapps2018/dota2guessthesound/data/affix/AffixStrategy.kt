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
    val showHeroImages: Boolean = true,
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
    val isSuddenDeath: Boolean = false
)

data class AnswerValidationResult(
    val isCorrect: Boolean,
    val originalLogic: Boolean = true,
    val customMessage: String? = null
)

data class TimerConfiguration(
    val durationMs: Long,
    val showWarningAt: Long = durationMs / 4 // Show warning at 25% remaining
)

data class SoundLimitations(
    val maxPlays: Int,
    val warningMessage: String? = null
)
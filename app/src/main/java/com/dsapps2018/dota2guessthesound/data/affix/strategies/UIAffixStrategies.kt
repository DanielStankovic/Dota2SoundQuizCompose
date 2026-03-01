package com.dsapps2018.dota2guessthesound.data.affix.strategies

import com.dsapps2018.dota2guessthesound.data.affix.AffixStrategy
import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState
import com.dsapps2018.dota2guessthesound.data.affix.TimerConfiguration

/**
 * Affix that hides the sound counter
 */
class HiddenMarksAffixStrategy : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(showMarkedSoundCounter = false)
    }
}

/**
 * Affix that blurs hero images
 */
class BlurredVisionAffixStrategy(val blur: String?) : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(
            blurHeroImages = true,
            blurIntensity = blur?.toFloat() ?: 7f // Blur radius
        )
    }
}

/**
 * Affix that completely hides hero images
 */
class HiddenHeroAffixStrategy : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(showHeroImages = false)
    }
}

/**
 * Affix that hides the total count of correct sounds
 */
class UnknownCountAffixStrategy : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(
            showSoundCounter = false, // Don't show the "X/Y" format
        )
    }
}

/**
 * Affix that adds a visible timer to the game
 */
class RaceAgainstTimeAffixStrategy(val timer: String?) : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(showTimer = true)
    }
    
    override fun getTimerConfiguration(): TimerConfiguration {
        return TimerConfiguration(
            durationMs = (timer?.toInt() ?: 60) * 1000L, // 60 seconds
        )
    }
}
package com.dsapps2018.dota2guessthesound.data.affix

/**
 * Affix that limits the number of times sounds can be played
 */
class EchoLimitAffixStrategy : AffixStrategy {
    override fun getSoundLimitations(): SoundLimitations {
        return SoundLimitations(
            maxPlays = 10,
            warningMessage = "You have limited sound plays remaining!"
        )
    }
}

/**
 * Affix that inverts the answer logic - player must select WRONG sounds
 */
class MirrorModeAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(invertAnswerLogic = true)
    }
    
    override fun modifyAnswerValidation(
        selectedSounds: Set<Int>,
        correctSounds: Set<Int>,
        allSoundIds: Set<Int>,
        currentResult: AnswerValidationResult
    ): AnswerValidationResult {
        val incorrectSounds = allSoundIds - correctSounds
        val isCorrectInMirrorMode =
            selectedSounds.size == incorrectSounds.size &&
                incorrectSounds.containsAll(selectedSounds)

        return currentResult.copy(
            isCorrect = isCorrectInMirrorMode,
            originalLogic = false,
            customMessage = "Mirror Mode: Select the WRONG sounds!"
        )
    }
}

/**
 * Affix that reduces heart count (makes player more fragile)
 */
class FragileSpiritAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(modifiedHeartCount = 1) // Only 1 heart instead of default 2
    }
}

/**
 * Affix that causes instant game over on first wrong answer
 */
class SuddenDeathAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(modifiedHeartCount = 1, isSuddenDeath = true) // Only 1 chance
    }
}

/**
 * Affix that causes screen shake or other effects when sounds are played
 */
class SoundquakeAffixStrategy : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState
    }
    
    // Note: Screen shake would be implemented in the UI layer when sounds are played
}

/**
 * Affix that shows heroes mixed with decoy heroes
 */
class AmongHeroesAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        // This would require modifying the hero selection logic
        // Implementation would be in the ViewModel where heroes are selected
        return currentState
    }
}
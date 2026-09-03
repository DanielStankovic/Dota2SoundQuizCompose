package com.dsapps2018.dota2guessthesound.data.affix

/**
 * Affix that enables Echo Limit (finite sound plays).
 * Play budget is authored on Journey `echo_limit_offset` + `max_sounds` in JourneyRound;
 * [SoundLimitations] only signals that the mechanic is on (no Affix `data` budget).
 */
class EchoLimitAffixStrategy : AffixStrategy {
    override fun getSoundLimitations(): SoundLimitations {
        return SoundLimitations(
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
 * Affix that reduces heart count (makes player more fragile).
 * Extra Life Gate remains allowed after the last heart.
 */
class FragileSpiritAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(modifiedHeartCount = 1)
    }
}

/**
 * Affix that starts with one heart and disables the Extra Life Gate —
 * first wrong submit ends the Journey Round.
 */
class SuddenDeathAffixStrategy : AffixStrategy {
    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(
            modifiedHeartCount = 1,
            extraLifeGateAllowed = false,
        )
    }
}

/**
 * Soundquake family: interval timer reshuffles the board instead of ending the round.
 * [clearMarksOnQuake] / [drainHeartOnQuake] distinguish Aftershock / Fracture / Cataclysm.
 * Interval seconds come from Journey `timer_seconds` (legacy Affix `data.timer` fallback only).
 */
class SoundquakeAffixStrategy(
    private val clearMarksOnQuake: Boolean = false,
    private val drainHeartOnQuake: Boolean = false,
) : AffixStrategy {
    override fun modifyUI(currentState: AffixUIState): AffixUIState {
        return currentState.copy(showTimer = true)
    }

    override fun modifyGameplay(currentState: AffixGameState): AffixGameState {
        return currentState.copy(
            soundquakeClearsMarks = clearMarksOnQuake,
            soundquakeDrainsHeart = drainHeartOnQuake,
        )
    }

    override fun getTimerConfiguration(): TimerConfiguration {
        return TimerConfiguration(
            durationMs = DEFAULT_SOUNDQUAKE_INTERVAL_MS,
            endsRoundOnTimeout = false,
        )
    }

    private companion object {
        const val DEFAULT_SOUNDQUAKE_INTERVAL_MS = 45_000L
    }
}


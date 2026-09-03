package com.dsapps2018.dota2guessthesound.data.affix

/**
 * Affix that enables Echo Limit (finite sound plays).
 * Play budget = Journey `max_sounds` + `echo_limit_offset` (default Medium +5).
 */
class EchoLimitAffixStrategy : AffixStrategy {
    override fun resolveSoundPlayBudget(level: AffixLevelContext): Int {
        val offset = (level.echoLimitOffset ?: DEFAULT_ECHO_LIMIT_OFFSET).coerceAtLeast(0)
        return level.maxSounds + offset
    }

    private companion object {
        const val DEFAULT_ECHO_LIMIT_OFFSET = 5
    }
}

/**
 * Affix that inverts the answer logic - player must select WRONG sounds
 */
class MirrorModeAffixStrategy : AffixStrategy {
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
 * Interval seconds come from Journey `timer_seconds`; [DEFAULT_SOUNDQUAKE_INTERVAL_MS]
 * applies only when that level field is unset/non-positive.
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
        /** Used only when Journey `timer_seconds` is unset/non-positive. */
        const val DEFAULT_SOUNDQUAKE_INTERVAL_MS = 20_000L
    }
}

package com.dsapps2018.dota2guessthesound.data.journey

import kotlinx.coroutines.CoroutineScope

/**
 * Internal Soundquake interval handler owned by Journey Round.
 * Reshuffle, mark policy, FX event, heart drain, and interval restart stay here —
 * AffixEngine only supplies [clearsMarks] / [drainsHeart].
 */
internal class SoundquakeInterval(
    private val updateReady: ((JourneyRoundState.Ready) -> JourneyRoundState.Ready) -> Unit,
    private val emit: suspend (JourneyRoundEvent) -> Unit,
    private val applyHeartLoss: suspend (restartAfterGate: Boolean) -> Boolean,
) {
    var clearsMarks: Boolean = false
    var drainsHeart: Boolean = false

    fun reset() {
        clearsMarks = false
        drainsHeart = false
    }

    suspend fun onIntervalElapsed(
        ready: JourneyRoundState.Ready,
        roundScope: CoroutineScope?,
        restartInterval: (CoroutineScope) -> Unit,
    ) {
        val reshuffled = ready.game.soundList.shuffled()
        val marks = if (clearsMarks) {
            reshuffled.associate { it.soundModel.id to false }
        } else {
            // Marks are keyed by sound id — keep selections across position shuffle.
            ready.selectedMarks
        }
        updateReady {
            it.copy(
                game = it.game.copy(soundList = reshuffled),
                selectedMarks = marks,
                timer = it.timer?.copy(remainingMs = 0L, isPaused = true),
            )
        }

        // Fire FX after board update so LazyGrid animateItem + shake run together.
        emit(
            JourneyRoundEvent.Soundquake(
                strong = clearsMarks,
                drainedHeart = drainsHeart,
            )
        )

        if (drainsHeart) {
            val survived = applyHeartLoss(true)
            if (!survived) return
        }

        val scope = roundScope ?: return
        restartInterval(scope)
    }
}

package com.dsapps2018.dota2guessthesound.data.progress

import com.dsapps2018.dota2guessthesound.data.enums.GameModeEnum
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import javax.inject.Inject

/**
 * Deep in-process module for finishing a mode run: Player Progress write + optional
 * leaderboard enqueue. See docs/adr/0010-mode-result.md and ADR-0001 for batching.
 */
class ModeResult @Inject constructor(
    private val playerProgressRepository: PlayerProgressRepository,
    private val leaderboardRepository: LeaderboardRepository,
) {

    suspend fun submitQuiz(score: Int) {
        playerProgressRepository.recordQuizResult(score)
        if (score > 0) {
            leaderboardRepository.updateLeaderboard(
                score.toDouble(),
                GameModeEnum.QUIZ.gameCode,
            )
        }
    }

    suspend fun submitInvoker(score: Int) {
        playerProgressRepository.recordInvokerResult(score)
        if (score > 0) {
            leaderboardRepository.updateLeaderboard(
                score.toDouble(),
                GameModeEnum.INVOKER.gameCode,
            )
        }
    }

    suspend fun submitFastFinger(guessed: Int, total: Int, time: Int) {
        val currentScore = playerProgressRepository.calculateFastFingerScore(guessed, total)
        playerProgressRepository.recordFastFingerResult(guessed, total, time)
        if (currentScore > 0.0) {
            leaderboardRepository.updateLeaderboard(
                currentScore,
                gameModeFromTime(time).gameCode,
            )
        }
    }

    suspend fun submitJourneyLevel(level: Int) {
        playerProgressRepository.advanceJourney(level)
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double =
        playerProgressRepository.calculateFastFingerScore(guessed, total)

    private fun gameModeFromTime(time: Int): GameModeEnum = when (time) {
        30 -> GameModeEnum.FF_30
        60 -> GameModeEnum.FF_60
        90 -> GameModeEnum.FF_90
        else -> GameModeEnum.FF_30
    }
}

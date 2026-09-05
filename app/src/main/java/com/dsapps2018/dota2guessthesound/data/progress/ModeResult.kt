package com.dsapps2018.dota2guessthesound.data.progress

import com.dsapps2018.dota2guessthesound.data.enums.GameModeEnum
import com.dsapps2018.dota2guessthesound.data.model.JourneyProgressWrite
import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import javax.inject.Inject

data class JourneyWinResult(
    val progress: JourneyProgressWrite,
    val clearTimeMs: Long,
    val isNewPersonalBest: Boolean,
)

/**
 * Deep in-process module for finishing a mode run: Player Progress write + optional
 * leaderboard / Level Clear Time enqueue. See docs/adr/0010-mode-result.md,
 * ADR-0001 for batching, ADR-0016 for Level Clear Time.
 */
class ModeResult @Inject constructor(
    private val playerProgressRepository: PlayerProgressRepository,
    private val leaderboardRepository: LeaderboardRepository,
    private val levelClearTime: LevelClearTime,
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

    suspend fun submitJourneyLevel(level: Int, clearTimeMs: Long): JourneyWinResult {
        val progress = playerProgressRepository.recordJourneyWin(level)
        val isNewPersonalBest = try {
            levelClearTime.recordWin(level, clearTimeMs)
        } catch (_: Exception) {
            false
        }
        return JourneyWinResult(
            progress = progress,
            clearTimeMs = clearTimeMs,
            isNewPersonalBest = isNewPersonalBest,
        )
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

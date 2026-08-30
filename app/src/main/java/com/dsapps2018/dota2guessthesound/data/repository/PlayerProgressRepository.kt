package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.UserDataDto
import com.dsapps2018.dota2guessthesound.data.api.response.getInitialServerUserData
import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.getInitialUserData
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import com.dsapps2018.dota2guessthesound.data.util.roundTo
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * Deep Data-layer module for Player Progress: local intents, observe, and cloud sync.
 * See docs/adr/0002-player-progress-repository.md and ADR-0001 for sync-on-adjustCoins.
 */
@Singleton
class PlayerProgressRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val userDataDao: UserDataDao,
) {

    companion object {
        private const val WEIGHT_MODIFIER = 2.0
    }

    val progress: Flow<PlayerProgress> = userDataDao.getUserDataFlow().map { it.toPlayerProgress() }

    fun journeyLevel(): Flow<Int> = userDataDao.getUserJourneyLevelFlow()

    fun lastSyncAt(): Flow<Long> = userDataDao.getLastSyncDate()

    suspend fun adjustCoins(delta: Int) {
        val local = getLocalUserData()
        local.coinValue += delta
        local.modifiedAt = getCurrentDate()
        userDataDao.update(local)
        sync()
    }

    suspend fun recordQuizResult(score: Int) {
        val local = getLocalUserData()
        if (score > local.quizScore) {
            local.quizScore = score
        }
        local.quizPlayed++
        local.coinValue += score
        local.modifiedAt = getCurrentDate()
        userDataDao.update(local)
    }

    suspend fun recordInvokerResult(score: Int) {
        val local = getLocalUserData()
        if (score > local.invokerScore) {
            local.invokerScore = score
        }
        local.invokerPlayed++
        local.modifiedAt = getCurrentDate()
        userDataDao.update(local)
    }

    suspend fun recordFastFingerResult(guessed: Int, total: Int, time: Int) {
        val local = getLocalUserData()
        val currentScore = calculateFastFingerScore(guessed, total)
        val savedScore = when (time) {
            30 -> local.thirtySecondsScore
            60 -> local.sixtySecondsScore
            90 -> local.ninetySecondsScore
            else -> local.thirtySecondsScore
        }

        if (currentScore > savedScore) {
            when (time) {
                30 -> local.thirtySecondsScore = currentScore
                60 -> local.sixtySecondsScore = currentScore
                90 -> local.ninetySecondsScore = currentScore
            }
        }
        when (time) {
            30 -> {
                local.thirtyPlayed++
                if (currentScore > Constants.FF_30_SCORE_MIN) {
                    local.coinValue += time
                }
            }
            60 -> {
                local.sixtyPlayed++
                if (currentScore > Constants.FF_60_SCORE_MIN) {
                    local.coinValue += time
                }
            }
            90 -> {
                local.ninetyPlayed++
                if (currentScore > Constants.FF_90_SCORE_MIN) {
                    local.coinValue += time
                }
            }
        }
        local.modifiedAt = getCurrentDate()
        userDataDao.update(local)
    }

    suspend fun advanceJourney(level: Int) {
        val local = getLocalUserData()
        local.journeyLevel = level
        local.modifiedAt = getCurrentDate()
        userDataDao.update(local)
    }

    fun calculateFastFingerScore(guessed: Int, total: Int): Double {
        if (total == 0) return 0.0
        val accuracy = guessed.toDouble() / total
        return (guessed * accuracy.pow(WEIGHT_MODIFIER)).roundTo(2)
    }

    suspend fun sync() {
        getAuthUserId()?.let { userId ->
            val serverUserData = getServerUserData(userId)
            val localUserData = getLocalUserData()

            val maxQuizScore = maxOf(serverUserData.quizScore, localUserData.quizScore)
            val maxInvokerScore = maxOf(serverUserData.invokerScore, localUserData.invokerScore)
            val maxThirtySecondsScore =
                maxOf(serverUserData.thirtySecondsScore, localUserData.thirtySecondsScore)
            val maxSixtySecondsScore =
                maxOf(serverUserData.sixtySecondsScore, localUserData.sixtySecondsScore)
            val maxNinetySecondsScore =
                maxOf(serverUserData.ninetySecondsScore, localUserData.ninetySecondsScore)
            val maxJourneyLevel = maxOf(serverUserData.journeyLevel, localUserData.journeyLevel)

            serverUserData.apply {
                quizScore = maxQuizScore
                invokerScore = maxInvokerScore
                thirtySecondsScore = maxThirtySecondsScore
                sixtySecondsScore = maxSixtySecondsScore
                ninetySecondsScore = maxNinetySecondsScore
                journeyLevel = maxJourneyLevel
            }

            localUserData.apply {
                quizScore = maxQuizScore
                invokerScore = maxInvokerScore
                thirtySecondsScore = maxThirtySecondsScore
                sixtySecondsScore = maxSixtySecondsScore
                ninetySecondsScore = maxNinetySecondsScore
                journeyLevel = maxJourneyLevel
            }

            val newTimesPlayedQuiz =
                localUserData.quizPlayed - localUserData.syncedQuizPlayed
            serverUserData.quizPlayed += newTimesPlayedQuiz
            localUserData.quizPlayed = serverUserData.quizPlayed
            localUserData.syncedQuizPlayed = serverUserData.quizPlayed

            val newTimesPlayedInvoker =
                localUserData.invokerPlayed - localUserData.syncedInvokerPlayed
            serverUserData.invokerPlayed += newTimesPlayedInvoker
            localUserData.invokerPlayed = serverUserData.invokerPlayed
            localUserData.syncedInvokerPlayed = serverUserData.invokerPlayed

            val newTimesPlayedThirty =
                localUserData.thirtyPlayed - localUserData.syncedThirtyPlayed
            serverUserData.thirtyPlayed += newTimesPlayedThirty
            localUserData.thirtyPlayed = serverUserData.thirtyPlayed
            localUserData.syncedThirtyPlayed = serverUserData.thirtyPlayed

            val newTimesPlayedSixty =
                localUserData.sixtyPlayed - localUserData.syncedSixtyPlayed
            serverUserData.sixtyPlayed += newTimesPlayedSixty
            localUserData.sixtyPlayed = serverUserData.sixtyPlayed
            localUserData.syncedSixtyPlayed = serverUserData.sixtyPlayed

            val newTimesPlayedNinety =
                localUserData.ninetyPlayed - localUserData.syncedNinetyPlayed
            serverUserData.ninetyPlayed += newTimesPlayedNinety
            localUserData.ninetyPlayed = serverUserData.ninetyPlayed
            localUserData.syncedNinetyPlayed = serverUserData.ninetyPlayed

            val newCoinValue =
                localUserData.coinValue - localUserData.syncedCoinValue
            serverUserData.coinValue += newCoinValue
            localUserData.coinValue = serverUserData.coinValue
            localUserData.syncedCoinValue = serverUserData.coinValue

            localUserData.userId = userId

            val modifiedDate = getCurrentDate()
            localUserData.modifiedAt = modifiedDate
            localUserData.lastSyncAt = System.currentTimeMillis()
            serverUserData.modifiedAt = modifiedDate

            userDataDao.update(localUserData)
            updateServerData(serverUserData)
        }
    }

    suspend fun createServerUserData(userId: String) {
        val serverUserData = getInitialServerUserData(userId)
        postgrest.from(Constants.TABLE_GAME_DATA).insert(serverUserData)
        // Reset synced* so logout → delete server row → login does not double-count deltas.
        val localUserData = getLocalUserData()
        localUserData.syncedQuizPlayed = 0
        localUserData.syncedInvokerPlayed = 0
        localUserData.syncedThirtyPlayed = 0
        localUserData.syncedSixtyPlayed = 0
        localUserData.syncedNinetyPlayed = 0
        localUserData.syncedCoinValue = 0
        userDataDao.update(localUserData)
    }

    suspend fun resetLocalUserData() {
        userDataDao.update(getInitialUserData())
    }

    private fun getAuthUserId(): String? = auth.currentUserOrNull()?.id

    private suspend fun getServerUserData(userId: String): UserDataDto {
        return postgrest
            .from(Constants.TABLE_GAME_DATA)
            .select(
                columns = Columns.list(
                    "modified_at",
                    "user_id",
                    "quiz_score",
                    "quiz_played",
                    "invoker_score",
                    "invoker_played",
                    "thirty_score",
                    "thirty_played",
                    "sixty_score",
                    "sixty_played",
                    "ninety_score",
                    "ninety_played",
                    "coin_value",
                    "journey_level"
                )
            ) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<UserDataDto>()
    }

    private suspend fun updateServerData(userDataDto: UserDataDto) {
        postgrest.from(Constants.TABLE_GAME_DATA).update(userDataDto) {
            filter {
                eq("user_id", userDataDto.userId)
            }
        }
    }

    private suspend fun getLocalUserData(): UserDataEntity {
        return userDataDao.getUserData() ?: getInitialUserData()
    }

    private fun UserDataEntity.toPlayerProgress(): PlayerProgress = PlayerProgress(
        quizScore = quizScore,
        quizPlayed = quizPlayed,
        invokerScore = invokerScore,
        invokerPlayed = invokerPlayed,
        thirtySecondsScore = thirtySecondsScore,
        thirtyPlayed = thirtyPlayed,
        sixtySecondsScore = sixtySecondsScore,
        sixtyPlayed = sixtyPlayed,
        ninetySecondsScore = ninetySecondsScore,
        ninetyPlayed = ninetyPlayed,
        coinValue = coinValue,
        journeyLevel = journeyLevel,
        lastSyncAt = lastSyncAt,
    )
}

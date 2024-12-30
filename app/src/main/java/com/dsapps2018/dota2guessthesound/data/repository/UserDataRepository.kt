package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.UserDataDto
import com.dsapps2018.dota2guessthesound.data.api.response.getInitialServerUserData
import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.getInitialUserData
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val userDataDao: UserDataDao
) {

    private fun getAuthUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    private suspend fun getServerUserData(userId: String): UserDataDto {
        try {
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
                    )
                ) {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeSingle<UserDataDto>()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun updateServerData(userDataDto: UserDataDto) {
        try {
            postgrest.from(Constants.TABLE_GAME_DATA).update(userDataDto) {
                filter {
                    eq("user_id", userDataDto.userId)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getLocalUserData(): UserDataEntity {
        return userDataDao.getUserData() ?: getInitialUserData()
    }

    suspend fun syncUserData() {
        getAuthUserId()?.let { userId ->
            try {
                val serverUserData = getServerUserData(userId)
                val localUserData = getLocalUserData()

                //HighScore update
                val maxQuizScore = maxOf(serverUserData.quizScore, localUserData.quizScore)
                val maxInvokerScore = maxOf(serverUserData.invokerScore, localUserData.invokerScore)
                val maxThirtySecondsScore =
                    maxOf(serverUserData.thirtySecondsScore, localUserData.thirtySecondsScore)
                val maxSixtySecondsScore =
                    maxOf(serverUserData.sixtySecondsScore, localUserData.sixtySecondsScore)
                val maxNinetySecondsScore =
                    maxOf(serverUserData.ninetySecondsScore, localUserData.ninetySecondsScore)
                //SERVER
                serverUserData.apply {
                    quizScore = maxQuizScore
                    invokerScore = maxInvokerScore
                    thirtySecondsScore = maxThirtySecondsScore
                    sixtySecondsScore = maxSixtySecondsScore
                    ninetySecondsScore = maxNinetySecondsScore
                }

                //LOCAL
                serverUserData.apply {
                    quizScore = maxQuizScore
                    invokerScore = maxInvokerScore
                    thirtySecondsScore = maxThirtySecondsScore
                    sixtySecondsScore = maxSixtySecondsScore
                    ninetySecondsScore = maxNinetySecondsScore
                }

//                serverUserData.quizScore = maxOf(serverUserData.quizScore, localUserData.quizScore)
//                serverUserData.invokerScore =
//                    maxOf(serverUserData.invokerScore, localUserData.invokerScore)
//                serverUserData.thirtySecondsScore =
//                    maxOf(serverUserData.thirtySecondsScore, localUserData.thirtySecondsScore)
//                serverUserData.sixtySecondsScore =
//                    maxOf(serverUserData.sixtySecondsScore, localUserData.sixtySecondsScore)
//                serverUserData.ninetySecondsScore =
//                    maxOf(serverUserData.ninetySecondsScore, localUserData.ninetySecondsScore)
//
//                //LOCAL
//                localUserData.quizScore = maxOf(serverUserData.quizScore, localUserData.quizScore)
//                localUserData.invokerScore =
//                    maxOf(serverUserData.invokerScore, localUserData.invokerScore)
//                localUserData.thirtySecondsScore =
//                    maxOf(serverUserData.thirtySecondsScore, localUserData.thirtySecondsScore)
//                localUserData.sixtySecondsScore =
//                    maxOf(serverUserData.sixtySecondsScore, localUserData.sixtySecondsScore)
//                localUserData.ninetySecondsScore =
//                    maxOf(serverUserData.ninetySecondsScore, localUserData.ninetySecondsScore)


                //Times played Update

                // Sync QUIZ timesPlayed by summing only once
                val newTimesPlayedQuiz =
                    localUserData.quizPlayed - localUserData.syncedQuizPlayed // Difference from the last sync
                serverUserData.quizPlayed += newTimesPlayedQuiz
                localUserData.quizPlayed = serverUserData.quizPlayed // Update local to match server
                localUserData.syncedQuizPlayed = serverUserData.quizPlayed // Update synced value

                // Sync INVOKER timesPlayed by summing only once
                val newTimesPlayedInvoker =
                    localUserData.invokerPlayed - localUserData.syncedInvokerPlayed
                serverUserData.invokerPlayed += newTimesPlayedInvoker
                localUserData.invokerPlayed = serverUserData.invokerPlayed
                localUserData.syncedInvokerPlayed = serverUserData.invokerPlayed

                // Sync 30 sec timesPlayed by summing only once
                val newTimesPlayedThirty =
                    localUserData.thirtyPlayed - localUserData.syncedThirtyPlayed
                serverUserData.thirtyPlayed += newTimesPlayedThirty
                localUserData.thirtyPlayed = serverUserData.thirtyPlayed
                localUserData.syncedThirtyPlayed = serverUserData.thirtyPlayed

                // Sync 60 sec timesPlayed by summing only once
                val newTimesPlayedSixty =
                    localUserData.sixtyPlayed - localUserData.syncedSixtyPlayed
                serverUserData.sixtyPlayed += newTimesPlayedSixty
                localUserData.sixtyPlayed = serverUserData.sixtyPlayed
                localUserData.syncedSixtyPlayed = serverUserData.sixtyPlayed


                // Sync 90 sec timesPlayed by summing only once
                val newTimesPlayedNinety =
                    localUserData.ninetyPlayed - localUserData.syncedNinetyPlayed
                serverUserData.ninetyPlayed += newTimesPlayedNinety
                localUserData.ninetyPlayed = serverUserData.ninetyPlayed
                localUserData.syncedNinetyPlayed = serverUserData.ninetyPlayed

                // Sync coinValue by summing only once
                val newCoinValue =
                    localUserData.coinValue - localUserData.syncedCoinValue
                serverUserData.coinValue += newCoinValue
                localUserData.coinValue = serverUserData.coinValue
                localUserData.syncedCoinValue = serverUserData.coinValue

                //Sync Local user ID
                localUserData.userId = userId

                //Sync modifiedDate and lastSynced fields
                val modifiedDate = getCurrentDate()
                localUserData.modifiedAt = modifiedDate
                localUserData.lastSyncAt = System.currentTimeMillis()

                serverUserData.modifiedAt = modifiedDate

                userDataDao.update(localUserData)
                updateServerData(serverUserData)

            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun createServerUserData(userId: String) {
        try {
            val serverUserData = getInitialServerUserData(userId)
            postgrest.from(Constants.TABLE_GAME_DATA).insert(serverUserData)
            //This has to be done because there is a bug when performing steps:
            //1. Have some user data locally where any played number is above 0
            //2. Logout from the app
            //3. Delete user data from the server for that user
            //4. Log back in. Data sync is then performed and wrong data is sent to server.
            // This number stays above 0 and needs to be reset
            val localUserData = getLocalUserData()
            localUserData.syncedQuizPlayed = 0
            localUserData.syncedInvokerPlayed = 0
            localUserData.syncedThirtyPlayed = 0
            localUserData.syncedSixtyPlayed = 0
            localUserData.syncedNinetyPlayed = 0
            localUserData.syncedCoinValue = 0
            userDataDao.update(localUserData)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateCoinValue(value: Int) {
        val localUserData = getLocalUserData()
        localUserData.coinValue += value
        userDataDao.update(localUserData)
    }
}
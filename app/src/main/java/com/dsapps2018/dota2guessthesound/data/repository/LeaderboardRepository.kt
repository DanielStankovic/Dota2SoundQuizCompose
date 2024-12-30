package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.request.LeaderboardDetailsDto
import com.dsapps2018.dota2guessthesound.data.dao.GameModeDao
import com.dsapps2018.dota2guessthesound.data.dao.LeaderboardDao
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardDetailsEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val leaderboardDao: LeaderboardDao,
    private val gameModeDao: GameModeDao
) {

    private fun getAuthUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    suspend fun updateLeaderboard(score: Double, gameModeCode: String) {
        try {
            val userId = getAuthUserId()
            val leaderboardId = leaderboardDao.getLeaderboard()?.id
            val gameModeId = gameModeDao.getGameModeIdByCode(gameModeCode)
            if (leaderboardId == null) return

            val leaderboardDetails = LeaderboardDetailsEntity(
                leaderboardId = leaderboardId,
                gameModeId = gameModeId,
                userId = userId,
                score = score,
                createdDate = getCurrentDate(),
                isSent = false
            )

            leaderboardDao.insertDetails(leaderboardDetails)

            //This will only send details that have user_id not null, so it is safe to call it anyway
            sendUnsentDetails()

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserIdAndSendData(userId: String) {
        try {
            leaderboardDao.updateUserId(userId)
            sendUnsentDetails()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun sendUnsentDetails() {
        try {
            val unsentDetailsLocal = leaderboardDao.getAllUnsentDetails()
            if (unsentDetailsLocal.isEmpty()) return
            val unsentDetailsServer = unsentDetailsLocal.map { details ->
                LeaderboardDetailsDto(
                    details.leaderboardId, details.userId!!, details.gameModeId, details.score
                )
            }

            postgrest.from(Constants.TABLE_LEADERBOARD_DETAILS).insert(unsentDetailsServer)

            leaderboardDao.deleteSentDetails(unsentDetailsLocal)

        } catch (e: Exception) {
            throw e
        }
    }

}
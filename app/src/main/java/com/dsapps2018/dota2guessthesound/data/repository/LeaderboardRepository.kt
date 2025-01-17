package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.request.LeaderboardDetailsDto
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardDto
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardHistoryDto
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.data.dao.GameModeDao
import com.dsapps2018.dota2guessthesound.data.dao.LeaderboardDetailsDao
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardDetailsEntity
import com.dsapps2018.dota2guessthesound.data.model.LeaderboardHistoryModel
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import com.dsapps2018.dota2guessthesound.data.util.getMonthYearStringFromStringDate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Columns.Companion.raw
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val leaderboardDetailsDao: LeaderboardDetailsDao,
    private val gameModeDao: GameModeDao
) {

    private fun getAuthUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    suspend fun updateLeaderboard(score: Double, gameModeCode: String) {
        try {
            val userId = getAuthUserId()
            val gameModeId = gameModeDao.getGameModeIdByCode(gameModeCode)

            val leaderboardDetails = LeaderboardDetailsEntity(
                gameModeId = gameModeId,
                userId = userId,
                score = score,
                createdDate = getCurrentDate(),
                isSent = false
            )

            leaderboardDetailsDao.insertDetails(leaderboardDetails)

            //This will only send details that have user_id not null, so it is safe to call it anyway
            sendUnsentDetails()

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserIdAndSendData(userId: String) {
        try {
            leaderboardDetailsDao.updateUserId(userId)
            sendUnsentDetails()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun sendUnsentDetails() {
        try {
            val unsentDetailsLocal = leaderboardDetailsDao.getAllUnsentDetails()
            if (unsentDetailsLocal.isEmpty()) return
            val unsentDetailsServer = unsentDetailsLocal.map { details ->
                LeaderboardDetailsDto(
                    details.userId!!, details.gameModeId, details.score, details.createdDate
                )
            }

            val jsonData = Json.encodeToJsonElement(unsentDetailsServer)

            postgrest.rpc(
                function = "insert_leaderboard_details",
                parameters = mapOf("data_list" to jsonData)
            )

            leaderboardDetailsDao.deleteSentDetails(unsentDetailsLocal)

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchTop10LeaderboardStandings(leaderboardId: Int?): List<LeaderboardStandingDto> {
        try {
            return postgrest.rpc(
                function = "get_top_10_leaderboard_standings",
                parameters = buildJsonObject {
                    put("current_user_id", getAuthUserId()!!)
                    put("lb_id", leaderboardId)
                }
            ).decodeList<LeaderboardStandingDto>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchCurrentUserLeaderboardStandings(leaderboardId: Int?): List<LeaderboardStandingDto> {
        try {
            return postgrest.rpc(
                function = "get_user_leaderboard_standings",
                parameters = buildJsonObject {
                    put("current_user_id", getAuthUserId()!!)
                    put("lb_id", leaderboardId)
                }
            ).decodeList<LeaderboardStandingDto>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchLeaderboardData(leaderboardId: Int?): LeaderboardDto {
        try {
            return postgrest.rpc(
                function = "get_leaderboard_entry",
                parameters = buildJsonObject {
                    put("lb_id", leaderboardId)
                }
            ).decodeSingle()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchLeaderboardHistory(): List<LeaderboardHistoryModel> {
        try {
            return postgrest.from(Constants.TABLE_LEADERBOARD).select(
                columns = Columns.list(
                    "id",
                    "name",
                    "start_at"
                )
            ) {
                filter {
                    eq("active", false)
                    lt("start_at", "now()")
                }
                order("start_at", Order.ASCENDING)
            }.decodeList<LeaderboardHistoryDto>().map { historyDto ->
                LeaderboardHistoryModel(
                    historyDto.id,
                    historyDto.name ?: getMonthYearStringFromStringDate(historyDto.startAt)
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
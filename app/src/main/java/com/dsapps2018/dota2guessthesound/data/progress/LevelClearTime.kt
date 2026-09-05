package com.dsapps2018.dota2guessthesound.data.progress

import com.dsapps2018.dota2guessthesound.data.api.response.LevelClearTimeStandingDto
import com.dsapps2018.dota2guessthesound.data.dao.LevelClearTimeDao
import com.dsapps2018.dota2guessthesound.data.db.entity.LevelClearTimeEntity
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deep Data-layer module for Level Clear Time: local best, upsert-if-faster RPC,
 * top 10 + own rank. See docs/adr/0016-level-clear-time.md.
 */
@Singleton
class LevelClearTime @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val dao: LevelClearTimeDao,
) {

    fun observePersonalBests(): Flow<Map<Int, Long>> =
        dao.observeAll().map { rows -> rows.associate { it.level to it.clearTimeMs } }

    /**
     * Keep a local best if [clearTimeMs] is strictly faster. Always attempts to flush
     * unsent rows (this win, plus any earlier failed upserts).
     * @return true when this run is a new personal best.
     */
    suspend fun recordWin(level: Int, clearTimeMs: Long): Boolean {
        var isNewBest = false
        if (clearTimeMs > 0L) {
            val existing = dao.get(level)
            isNewBest = existing == null || clearTimeMs < existing.clearTimeMs
            if (isNewBest) {
                dao.upsert(
                    LevelClearTimeEntity(
                        level = level,
                        clearTimeMs = clearTimeMs,
                        isSent = false,
                    )
                )
            }
        }
        sendUnsent()
        return isNewBest
    }

    suspend fun sendUnsent() {
        if (auth.currentUserOrNull()?.id == null) return
        val unsent = dao.getUnsent()
        if (unsent.isEmpty()) return
        unsent.forEach { row ->
            try {
                postgrest.rpc(
                    function = "upsert_level_clear_time",
                    parameters = buildJsonObject {
                        put("p_level", row.level)
                        put("p_clear_time_ms", row.clearTimeMs.toInt())
                    }
                )
                dao.markSent(row.level)
            } catch (_: Exception) {
                // Stay unsent; Sync Session / next win retries.
            }
        }
    }

    suspend fun fetchBoard(level: Int): List<LevelClearTimeStandingDto> {
        val top10 = postgrest.rpc(
            function = "get_top_10_level_clear_times",
            parameters = buildJsonObject { put("p_level", level) },
        ).decodeList<LevelClearTimeStandingDto>()
        val mine = postgrest.rpc(
            function = "get_user_level_clear_time",
            parameters = buildJsonObject { put("p_level", level) },
        ).decodeList<LevelClearTimeStandingDto>()
        return if (top10.any { it.isCurrentUser }) top10 else top10 + mine
    }

    suspend fun clearLocal() {
        dao.deleteAll()
    }
}

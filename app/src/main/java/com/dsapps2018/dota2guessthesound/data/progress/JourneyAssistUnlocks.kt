package com.dsapps2018.dota2guessthesound.data.progress

import com.dsapps2018.dota2guessthesound.data.api.response.JourneyAssistUnlockDto
import com.dsapps2018.dota2guessthesound.data.dao.JourneyAssistUnlockDao
import com.dsapps2018.dota2guessthesound.data.db.entity.JourneyAssistUnlockEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

data class JourneyAssistUnlockFlags(
    val countUnlocked: Boolean = false,
    val primerUnlocked: Boolean = false,
)

/**
 * Deep Data-layer module for per-level Journey Assist unlocks.
 * See docs/adr/0022-journey-assists.md.
 */
@Singleton
class JourneyAssistUnlocks @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val dao: JourneyAssistUnlockDao,
) {

    fun observe(level: Int): Flow<JourneyAssistUnlockFlags> =
        dao.observe(level).map { it.toFlags() }

    suspend fun flags(level: Int): JourneyAssistUnlockFlags =
        dao.get(level).toFlags()

    /** OR-merge local unlock; flush when signed in. */
    suspend fun unlockCount(level: Int) = unlock(level, count = true, primer = false)

    suspend fun unlockPrimer(level: Int) = unlock(level, count = false, primer = true)

    private suspend fun unlock(level: Int, count: Boolean, primer: Boolean) {
        if (level < 1) return
        val existing = dao.get(level)
        val nextCount = (existing?.countUnlocked == true) || count
        val nextPrimer = (existing?.primerUnlocked == true) || primer
        if (existing != null &&
            existing.countUnlocked == nextCount &&
            existing.primerUnlocked == nextPrimer
        ) {
            return
        }
        dao.upsert(
            JourneyAssistUnlockEntity(
                level = level,
                countUnlocked = nextCount,
                primerUnlocked = nextPrimer,
                isSent = false,
            )
        )
        sendUnsent()
    }

    suspend fun sendUnsent() {
        if (auth.currentUserOrNull()?.id == null) return
        val unsent = dao.getUnsent()
        if (unsent.isEmpty()) return
        unsent.forEach { row ->
            try {
                postgrest.rpc(
                    function = "upsert_journey_assist_unlock",
                    parameters = buildJsonObject {
                        put("p_level", row.level)
                        put("p_count_unlocked", row.countUnlocked)
                        put("p_primer_unlocked", row.primerUnlocked)
                    }
                )
                dao.markSent(row.level)
            } catch (_: Exception) {
                // Stay unsent; Sync Session / Auth attach / next unlock retries.
            }
        }
    }

    /**
     * Pull signed-in player's unlocks into Room. OR-merge; never clears a local true.
     * Call after [sendUnsent].
     */
    suspend fun hydrate() {
        val uid = auth.currentUserOrNull()?.id ?: return
        val remote = try {
            postgrest.from(Constants.TABLE_JOURNEY_ASSIST_UNLOCK)
                .select(Columns.list("level", "count_unlocked", "primer_unlocked")) {
                    filter { eq("user_id", uid) }
                }
                .decodeList<JourneyAssistUnlockDto>()
        } catch (_: Exception) {
            return
        }
        remote.forEach { row -> mergeRemote(row) }
    }

    suspend fun clearLocal() {
        dao.deleteAll()
    }

    private suspend fun mergeRemote(row: JourneyAssistUnlockDto) {
        if (row.level < 1) return
        if (!row.countUnlocked && !row.primerUnlocked) return
        val local = dao.get(row.level)
        val nextCount = (local?.countUnlocked == true) || row.countUnlocked
        val nextPrimer = (local?.primerUnlocked == true) || row.primerUnlocked
        val dirty = local?.isSent == false
        if (local != null &&
            local.countUnlocked == nextCount &&
            local.primerUnlocked == nextPrimer &&
            !dirty
        ) {
            return
        }
        dao.upsert(
            JourneyAssistUnlockEntity(
                level = row.level,
                countUnlocked = nextCount,
                primerUnlocked = nextPrimer,
                isSent = !dirty,
            )
        )
    }

    private fun JourneyAssistUnlockEntity?.toFlags() = JourneyAssistUnlockFlags(
        countUnlocked = this?.countUnlocked == true,
        primerUnlocked = this?.primerUnlocked == true,
    )
}

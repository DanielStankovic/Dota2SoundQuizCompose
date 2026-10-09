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
    val soundSamplerUnlocked: Boolean = false,
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
    suspend fun unlockCount(level: Int) = unlock(level, count = true, soundSampler = false)

    suspend fun unlockSoundSampler(level: Int) = unlock(level, count = false, soundSampler = true)

    private suspend fun unlock(level: Int, count: Boolean, soundSampler: Boolean) {
        if (level < 1) return
        val existing = dao.get(level)
        val nextCount = (existing?.countUnlocked == true) || count
        val nextSoundSampler = (existing?.soundSamplerUnlocked == true) || soundSampler
        if (existing != null &&
            existing.countUnlocked == nextCount &&
            existing.soundSamplerUnlocked == nextSoundSampler
        ) {
            return
        }
        dao.upsert(
            JourneyAssistUnlockEntity(
                level = level,
                countUnlocked = nextCount,
                soundSamplerUnlocked = nextSoundSampler,
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
                        put("p_primer_unlocked", row.soundSamplerUnlocked)
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
        if (!row.countUnlocked && !row.soundSamplerUnlocked) return
        val local = dao.get(row.level)
        val nextCount = (local?.countUnlocked == true) || row.countUnlocked
        val nextSoundSampler = (local?.soundSamplerUnlocked == true) || row.soundSamplerUnlocked
        val dirty = local?.isSent == false
        if (local != null &&
            local.countUnlocked == nextCount &&
            local.soundSamplerUnlocked == nextSoundSampler &&
            !dirty
        ) {
            return
        }
        dao.upsert(
            JourneyAssistUnlockEntity(
                level = row.level,
                countUnlocked = nextCount,
                soundSamplerUnlocked = nextSoundSampler,
                isSent = !dirty,
            )
        )
    }

    private fun JourneyAssistUnlockEntity?.toFlags() = JourneyAssistUnlockFlags(
        countUnlocked = this?.countUnlocked == true,
        soundSamplerUnlocked = this?.soundSamplerUnlocked == true,
    )
}

package com.dsapps2018.dota2guessthesound.data.progress

import com.dsapps2018.dota2guessthesound.data.analytics.ProductAnalytics
import com.dsapps2018.dota2guessthesound.data.api.response.StoryIssueRatingDto
import com.dsapps2018.dota2guessthesound.data.api.response.StoryIssueScoreDto
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueRatingDao
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueRatingEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deep Data-layer module for Issue Rating + Issue Score refresh.
 * See docs/adr/0021-issue-rating.md.
 */
@Singleton
class IssueRating @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val ratingDao: StoryIssueRatingDao,
    private val issueDao: StoryIssueDao,
    private val productAnalytics: ProductAnalytics,
) {

    suspend fun myStars(issueId: Int): Int? =
        ratingDao.get(issueId)?.stars?.takeIf { it in 1..5 }

    /**
     * Set or overwrite the player's Issue Rating. Same star as current → no-op.
     * @return true when the local Rating changed (triggers confirm animation).
     */
    suspend fun rate(issueId: Int, stars: Int): Boolean {
        if (stars !in 1..5) return false
        if (auth.currentUserOrNull()?.id == null) return false
        val existing = ratingDao.get(issueId)
        if (existing?.stars == stars) return false

        val previous = existing?.stars
        ratingDao.upsert(
            StoryIssueRatingEntity(
                issueId = issueId,
                stars = stars,
                isSent = false,
            )
        )
        applyLocalScoreDelta(issueId, previousStars = previous, newStars = stars)
        productAnalytics.issueRate(
            issueId = issueId,
            issueSlug = issueDao.getById(issueId)?.slug.orEmpty(),
            stars = stars,
            isRerate = previous != null,
        )
        sendUnsent()
        return true
    }

    suspend fun sendUnsent() {
        if (auth.currentUserOrNull()?.id == null) return
        val unsent = ratingDao.getUnsent()
        if (unsent.isEmpty()) return
        unsent.forEach { row ->
            try {
                postgrest.rpc(
                    function = "upsert_issue_rating",
                    parameters = buildJsonObject {
                        put("p_issue_id", row.issueId)
                        put("p_stars", row.stars)
                    }
                )
                ratingDao.markSent(row.issueId)
            } catch (_: Exception) {
                // Stay unsent; Sync Session / Auth attach / next rate retries.
            }
        }
        // Pull server aggregates after a successful flush batch so cards stay accurate.
        if (ratingDao.getUnsent().isEmpty()) {
            refreshScores()
        }
    }

    /**
     * Pull Issue Score columns for active Issues into Room (does not touch art paths).
     * Skips Issues with a dirty local Rating so optimistic aggregates survive a failed flush.
     */
    suspend fun refreshScores() {
        val dirtyIssueIds = ratingDao.getUnsent().map { it.issueId }.toSet()
        val remote = try {
            postgrest.from(Constants.TABLE_STORY_ISSUE)
                .select(Columns.list("id", "rating_sum", "rating_count")) {
                    filter { eq("active", true) }
                }
                .decodeList<StoryIssueScoreDto>()
        } catch (_: Exception) {
            return
        }
        remote.forEach { row ->
            if (row.id in dirtyIssueIds) return@forEach
            issueDao.updateScore(
                id = row.id,
                ratingSum = row.ratingSum.coerceAtLeast(0L),
                ratingCount = row.ratingCount.coerceAtLeast(0),
            )
        }
    }

    /**
     * Hydrate the signed-in player's Issue Ratings from the server.
     * Never overwrites a dirtier local unsent row.
     */
    suspend fun hydrateMine() {
        val uid = auth.currentUserOrNull()?.id ?: return
        val remote = try {
            postgrest.from(Constants.TABLE_STORY_ISSUE_RATING)
                .select(Columns.list("issue_id", "stars")) {
                    filter { eq("user_id", uid) }
                }
                .decodeList<StoryIssueRatingDto>()
        } catch (_: Exception) {
            return
        }
        remote.forEach { row ->
            if (row.stars !in 1..5) return@forEach
            val local = ratingDao.get(row.issueId)
            when {
                local == null -> ratingDao.upsert(
                    StoryIssueRatingEntity(
                        issueId = row.issueId,
                        stars = row.stars,
                        isSent = true,
                    )
                )
                !local.isSent -> Unit
                local.stars != row.stars -> ratingDao.upsert(
                    StoryIssueRatingEntity(
                        issueId = row.issueId,
                        stars = row.stars,
                        isSent = true,
                    )
                )
            }
        }
    }

    suspend fun clearLocal() {
        ratingDao.deleteAll()
    }

    private suspend fun applyLocalScoreDelta(
        issueId: Int,
        previousStars: Int?,
        newStars: Int,
    ) {
        val issue = issueDao.getById(issueId) ?: return
        val (sum, count) = when (previousStars) {
            null -> issue.ratingSum + newStars to issue.ratingCount + 1
            else -> issue.ratingSum - previousStars + newStars to issue.ratingCount
        }
        issueDao.updateScore(
            id = issueId,
            ratingSum = sum.coerceAtLeast(0L),
            ratingCount = count.coerceAtLeast(0),
        )
    }
}

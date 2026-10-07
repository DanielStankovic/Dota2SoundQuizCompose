package com.dsapps2018.dota2guessthesound.data.progress

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.os.EnvironmentCompat
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.StoryChapterDto
import com.dsapps2018.dota2guessthesound.data.api.response.StoryChapterI18nDto
import com.dsapps2018.dota2guessthesound.data.api.response.StoryIssueDto
import com.dsapps2018.dota2guessthesound.data.api.response.StoryIssueI18nDto
import com.dsapps2018.dota2guessthesound.data.api.response.StoryProgressDto
import com.dsapps2018.dota2guessthesound.data.dao.StoryChapterDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryChapterI18nDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueI18nDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryProgressDao
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterI18nEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueI18nEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryProgressEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getCurrentDate
import com.dsapps2018.dota2guessthesound.data.util.getInitialModifiedDate
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.downloadPublicTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Deep Data-layer module for the Journey Issue catalog, local art, and Issue Progress.
 * See docs/adr/0019-journey-issue.md.
 */
@Singleton
class JourneyIssue @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth,
    private val issueDao: StoryIssueDao,
    private val chapterDao: StoryChapterDao,
    private val issueI18nDao: StoryIssueI18nDao,
    private val chapterI18nDao: StoryChapterI18nDao,
    private val progressDao: StoryProgressDao,
) {

    suspend fun syncCatalog() {
        syncIssues()
        syncChapters()
        val locales = catalogLocales()
        locales.forEach { locale ->
            syncIssueI18n(locale)
            syncChapterI18n(locale)
        }
    }

    suspend fun hydrateProgress() {
        val uid = auth.currentUserOrNull()?.id ?: return
        val remote = try {
            postgrest.from(Constants.TABLE_STORY_PROGRESS)
                .select(
                    Columns.list(
                        "user_id",
                        "active_issue_id",
                        "active_unlocked",
                        "owed_grants",
                        "completed_issue_ids",
                        "reading_locale",
                        "modified_at",
                    )
                ) {
                    filter { eq("user_id", uid) }
                }
                .decodeList<StoryProgressDto>()
                .firstOrNull()
        } catch (_: Exception) {
            return
        }
        if (remote == null) {
            progressDao.upsert(emptyProgress(uid, getInitialModifiedDate()))
            return
        }
        progressDao.upsert(
            StoryProgressEntity(
                userId = remote.userId,
                activeIssueId = remote.activeIssueId,
                activeUnlocked = remote.activeUnlocked,
                owedGrants = remote.owedGrants,
                completedIssueIds = remote.completedIssueIds,
                readingLocale = remote.readingLocale.ifBlank { ENGLISH },
                modifiedAt = remote.modifiedAt,
            )
        )
    }

    suspend fun clearLocalProgress() {
        progressDao.deleteAll()
    }

    suspend fun clearLocalCatalog() {
        chapterI18nDao.truncateTable()
        issueI18nDao.truncateTable()
        chapterDao.truncateTable()
        issueDao.truncateTable()
        storyArtDirectory()?.deleteRecursively()
    }

    /**
     * Journey first-clear only. With an Active Issue, grants one Chapter.
     * With none, accrues an owed grant and asks for the Issue Picker.
     */
    suspend fun onFirstClear(): FirstClearOutcome {
        val progress = requireProgress() ?: return FirstClearOutcome.None
        val activeId = progress.activeIssueId
        if (activeId == null) {
            val updated = progress.copy(
                owedGrants = progress.owedGrants + 1,
                modifiedAt = getCurrentDate(),
            )
            persistProgress(updated)
            return if (hasPickableIssues(updated.completedIssueIds)) {
                FirstClearOutcome.ShowPicker
            } else {
                FirstClearOutcome.None
            }
        }
        val total = chapterDao.countForIssue(activeId)
        if (total <= 0) return FirstClearOutcome.None
        val grantedIndex = (progress.activeUnlocked + 1).coerceAtMost(total)
        val completed = if (grantedIndex >= total && activeId !in progress.completedIssueIds) {
            progress.completedIssueIds + activeId
        } else {
            progress.completedIssueIds
        }
        val updated = progress.copy(
            activeIssueId = if (grantedIndex >= total) null else activeId,
            activeUnlocked = if (grantedIndex >= total) 0 else grantedIndex,
            completedIssueIds = completed,
            modifiedAt = getCurrentDate(),
        )
        persistProgress(updated)
        return FirstClearOutcome.ShowReader(
            issueId = activeId,
            chapterIndex = grantedIndex,
            unlockedCount = grantedIndex,
            totalChapters = total,
        )
    }

    suspend fun pickIssue(issueId: Int): PickOutcome {
        val progress = requireProgress() ?: return PickOutcome.ShowPickerAgain
        if (progress.activeIssueId != null) {
            return readerOrPickerAfterPick(progress)
        }
        if (issueId in progress.completedIssueIds) {
            return PickOutcome.ShowPickerAgain
        }
        val total = chapterDao.countForIssue(issueId)
        if (total <= 0) return PickOutcome.ShowPickerAgain

        val applied = min(progress.owedGrants, total)
        val owedLeft = progress.owedGrants - applied
        val completedIssue = applied >= total
        val completed = if (completedIssue && issueId !in progress.completedIssueIds) {
            progress.completedIssueIds + issueId
        } else {
            progress.completedIssueIds
        }
        val updated = progress.copy(
            activeIssueId = if (completedIssue) null else issueId,
            activeUnlocked = if (completedIssue) 0 else applied,
            owedGrants = owedLeft,
            completedIssueIds = completed,
            modifiedAt = getCurrentDate(),
        )
        persistProgress(updated)

        if (owedLeft > 0 && updated.activeIssueId == null &&
            hasPickableIssues(updated.completedIssueIds)
        ) {
            return PickOutcome.ShowPickerAgain
        }
        if (applied <= 0) return PickOutcome.ShowPickerAgain
        return PickOutcome.ShowReader(
            issueId = issueId,
            chapterIndex = applied,
            unlockedCount = applied,
            totalChapters = total,
        )
    }

    suspend fun pickerItems(): List<IssuePickerItem> {
        val progress = requireProgress()
        val completed = progress?.completedIssueIds.orEmpty().toSet()
        val locale = progress?.readingLocale?.takeIf { it.isNotBlank() } ?: ENGLISH
        return issueDao.getActive()
            .filter { it.id !in completed }
            .map { issue ->
                val i18n = issueI18n(issue.id, locale)
                IssuePickerItem(
                    issueId = issue.id,
                    title = i18n?.title.orEmpty().ifBlank { issue.slug },
                    synopsis = i18n?.synopsis.orEmpty(),
                    chapterCount = chapterDao.countForIssue(issue.id),
                    coverFilePath = issue.coverFileLink,
                )
            }
    }

    /** Whether the Issue Library has an Active Issue and/or completed Issues. */
    suspend fun hasLibraryEntries(): Boolean {
        val progress = requireProgress() ?: return false
        if (progress.completedIssueIds.isNotEmpty()) return true
        val activeId = progress.activeIssueId ?: return false
        return issueDao.getById(activeId) != null
    }

    /**
     * Active Issue first, then completed (completion order), then locked unstarted
     * Issues (catalog sort) so players can preview upcoming serials.
     */
    suspend fun libraryItems(): List<IssueLibraryItem> {
        val progress = requireProgress() ?: return emptyList()
        val locale = progress.readingLocale.takeIf { it.isNotBlank() } ?: ENGLISH
        val completed = progress.completedIssueIds.toSet()
        val activeId = progress.activeIssueId
        val items = ArrayList<IssueLibraryItem>()
        val catalog = issueDao.getActive()
        val seen = HashSet<Int>()

        if (activeId != null) {
            libraryItem(
                issueId = activeId,
                locale = locale,
                unlockedCount = progress.activeUnlocked,
                access = IssueLibraryAccess.Active,
            )?.let {
                items += it
                seen += activeId
            }
        }

        for (completedId in progress.completedIssueIds) {
            if (completedId in seen) continue
            libraryItem(
                issueId = completedId,
                locale = locale,
                unlockedCount = chapterDao.countForIssue(completedId),
                access = IssueLibraryAccess.Completed,
            )?.let {
                items += it
                seen += completedId
            }
        }

        for (issue in catalog) {
            if (issue.id in seen || issue.id in completed) continue
            if (issue.id == activeId) continue
            libraryItem(
                issueId = issue.id,
                locale = locale,
                unlockedCount = 0,
                access = IssueLibraryAccess.Locked,
            )?.let { items += it }
        }
        return items
    }

    private suspend fun libraryItem(
        issueId: Int,
        locale: String,
        unlockedCount: Int,
        access: IssueLibraryAccess,
    ): IssueLibraryItem? {
        val issue = issueDao.getById(issueId) ?: return null
        val total = chapterDao.countForIssue(issueId)
        if (total <= 0) return null
        val unlocked = unlockedCount.coerceIn(0, total)
        if (access == IssueLibraryAccess.Completed && unlocked <= 0) return null
        val i18n = issueI18n(issueId, locale)
        return IssueLibraryItem(
            issueId = issueId,
            title = i18n?.title.orEmpty().ifBlank { issue.slug },
            synopsis = i18n?.synopsis.orEmpty(),
            chapterCount = total,
            unlockedCount = unlocked,
            coverFilePath = issue.coverFileLink,
            access = access,
        )
    }

    suspend fun chapterPage(
        issueId: Int,
        chapterIndex: Int,
        unlockedCount: Int,
    ): ChapterReaderPage? {
        val issue = issueDao.getById(issueId) ?: return null
        val total = chapterDao.countForIssue(issueId)
        if (total <= 0 || chapterIndex !in 1..unlockedCount || chapterIndex > total) return null
        val chapter = chapterDao.getByIssueAndIndex(issueId, chapterIndex) ?: return null
        val progress = requireProgress()
        val locale = progress?.readingLocale?.takeIf { it.isNotBlank() } ?: ENGLISH
        val i18nRow = chapterI18n(chapter.id, locale)
        val issueTitle = issueI18n(issueId, locale)?.title.orEmpty().ifBlank { issue.slug }
        return ChapterReaderPage(
            issueId = issueId,
            chapterIndex = chapterIndex,
            unlockedCount = unlockedCount.coerceAtMost(total),
            totalChapters = total,
            body = i18nRow?.body.orEmpty(),
            chapterTitle = i18nRow?.title.orEmpty(),
            backdropFilePath = issue.backdropFileLink,
            issueTitle = issueTitle,
        )
    }

    private suspend fun readerOrPickerAfterPick(progress: StoryProgressEntity): PickOutcome {
        val activeId = progress.activeIssueId ?: return PickOutcome.ShowPickerAgain
        val total = chapterDao.countForIssue(activeId)
        val unlocked = progress.activeUnlocked.coerceAtMost(total)
        if (unlocked <= 0) return PickOutcome.ShowPickerAgain
        return PickOutcome.ShowReader(
            issueId = activeId,
            chapterIndex = unlocked,
            unlockedCount = unlocked,
            totalChapters = total,
        )
    }

    private suspend fun hasPickableIssues(completedIds: List<Int>): Boolean {
        val completed = completedIds.toSet()
        return issueDao.getActive().any { it.id !in completed }
    }

    private suspend fun requireProgress(): StoryProgressEntity? {
        val uid = auth.currentUserOrNull()?.id ?: return null
        progressDao.get(uid)?.let { return it }
        val empty = emptyProgress(uid, getCurrentDate())
        progressDao.upsert(empty)
        return empty
    }

    private suspend fun persistProgress(entity: StoryProgressEntity) {
        progressDao.upsert(entity)
        try {
            postgrest.from(Constants.TABLE_STORY_PROGRESS).upsert(
                StoryProgressDto(
                    userId = entity.userId,
                    activeIssueId = entity.activeIssueId,
                    activeUnlocked = entity.activeUnlocked,
                    owedGrants = entity.owedGrants,
                    completedIssueIds = entity.completedIssueIds,
                    readingLocale = entity.readingLocale,
                    modifiedAt = entity.modifiedAt,
                )
            )
        } catch (_: Exception) {
            // Local row is source of truth until Sync Session / next write retries.
        }
    }

    private suspend fun issueI18n(issueId: Int, locale: String): StoryIssueI18nEntity? =
        issueI18nDao.get(issueId, locale)
            ?: if (locale != ENGLISH) issueI18nDao.get(issueId, ENGLISH) else null

    private suspend fun chapterI18n(chapterId: Int, locale: String): StoryChapterI18nEntity? =
        chapterI18nDao.get(chapterId, locale)
            ?: if (locale != ENGLISH) chapterI18nDao.get(chapterId, ENGLISH) else null

    private suspend fun catalogLocales(): List<String> {
        val preferred = auth.currentUserOrNull()?.id
            ?.let { progressDao.get(it)?.readingLocale }
            ?.takeIf { it.isNotBlank() }
            ?: ENGLISH
        return if (preferred == ENGLISH) listOf(ENGLISH) else listOf(ENGLISH, preferred)
    }

    private suspend fun syncIssues() {
        val modifiedDate = issueDao.getModifiedDate() ?: getInitialModifiedDate()
        val fetched = postgrest.from(Constants.TABLE_STORY_ISSUE)
            .select(
                Columns.list(
                    "id",
                    "slug",
                    "protagonist_caster_id",
                    "sort",
                    "cover_path",
                    "backdrop_path",
                    "modified_at",
                    "active",
                )
            ) {
                filter { gt("modified_at", modifiedDate) }
                order("modified_at", Order.ASCENDING)
            }
            .decodeList<StoryIssueDto>()
            .map { dto ->
                StoryIssueEntity(
                    id = dto.id,
                    slug = dto.slug,
                    protagonistCasterId = dto.protagonistCasterId,
                    sort = dto.sort,
                    coverPath = dto.coverPath,
                    backdropPath = dto.backdropPath,
                    coverFileLink = "",
                    backdropFileLink = "",
                    modifiedAt = dto.modifiedAt,
                    isActive = dto.isActive,
                )
            }
        if (fetched.isEmpty()) return
        issueDao.deleteAll(fetched)
        val active = fetched.filter { it.isActive }
        if (active.isEmpty()) return
        downloadArt(active)
        issueDao.insertAll(active)
    }

    private suspend fun syncChapters() {
        val modifiedDate = chapterDao.getModifiedDate() ?: getInitialModifiedDate()
        val fetched = postgrest.from(Constants.TABLE_STORY_CHAPTER)
            .select(Columns.list("id", "issue_id", "chapter_index", "modified_at")) {
                filter { gt("modified_at", modifiedDate) }
                order("modified_at", Order.ASCENDING)
            }
            .decodeList<StoryChapterDto>()
            .map { dto ->
                StoryChapterEntity(
                    id = dto.id,
                    issueId = dto.issueId,
                    chapterIndex = dto.chapterIndex,
                    modifiedAt = dto.modifiedAt,
                )
            }
        if (fetched.isEmpty()) return
        chapterDao.deleteAll(fetched)
        chapterDao.insertAll(fetched)
    }

    private suspend fun syncIssueI18n(locale: String) {
        val modifiedDate = issueI18nDao.getModifiedDate(locale) ?: getInitialModifiedDate()
        val fetched = postgrest.from(Constants.TABLE_STORY_ISSUE_I18N)
            .select(Columns.list("issue_id", "locale", "title", "synopsis", "modified_at")) {
                filter {
                    gt("modified_at", modifiedDate)
                    eq("locale", locale)
                }
                order("modified_at", Order.ASCENDING)
            }
            .decodeList<StoryIssueI18nDto>()
            .map { dto ->
                StoryIssueI18nEntity(
                    issueId = dto.issueId,
                    locale = dto.locale,
                    title = dto.title,
                    synopsis = dto.synopsis,
                    modifiedAt = dto.modifiedAt,
                )
            }
        if (fetched.isEmpty()) return
        issueI18nDao.deleteAll(fetched)
        issueI18nDao.insertAll(fetched)
    }

    private suspend fun syncChapterI18n(locale: String) {
        val modifiedDate = chapterI18nDao.getModifiedDate(locale) ?: getInitialModifiedDate()
        val fetched = postgrest.from(Constants.TABLE_STORY_CHAPTER_I18N)
            .select(Columns.list("chapter_id", "locale", "title", "body", "modified_at")) {
                filter {
                    gt("modified_at", modifiedDate)
                    eq("locale", locale)
                }
                order("modified_at", Order.ASCENDING)
            }
            .decodeList<StoryChapterI18nDto>()
            .map { dto ->
                StoryChapterI18nEntity(
                    chapterId = dto.chapterId,
                    locale = dto.locale,
                    title = dto.title,
                    body = dto.body,
                    modifiedAt = dto.modifiedAt,
                )
            }
        if (fetched.isEmpty()) return
        chapterI18nDao.deleteAll(fetched)
        chapterI18nDao.insertAll(fetched)
    }

    private suspend fun downloadArt(issues: List<StoryIssueEntity>) {
        val directory = storyArtDirectory()
            ?: throw Exception(context.getString(R.string.error_can_not_create_directory))
        coroutineScope {
            val semaphore = Semaphore(8)
            issues.map { issue ->
                async(Dispatchers.IO) {
                    semaphore.withPermit {
                        issue.coverFileLink = downloadFile(directory, issue.coverPath)
                        issue.backdropFileLink = downloadFile(directory, issue.backdropPath)
                    }
                }
            }.awaitAll()
        }
    }

    private suspend fun downloadFile(directory: File, remotePath: String): String {
        val local = File(directory, remotePath.replace('/', '_'))
        var attempt = 0
        while (true) {
            try {
                storage.from(Constants.BUCKET_STORY_ART).downloadPublicTo(remotePath, local)
                return local.path
            } catch (e: Exception) {
                attempt++
                if (attempt >= 3) throw e
            }
        }
    }

    private fun storyArtDirectory(): File? {
        val volumes = ContextCompat.getExternalFilesDirs(context, null)
        var mounted = false
        volumes.forEach { storage ->
            if (EnvironmentCompat.getStorageState(storage) != Environment.MEDIA_MOUNTED) {
                return@forEach
            }
            mounted = true
            val dir = File(storage, STORY_ART_DIR)
            if (!dir.exists() && !dir.mkdirs()) return@forEach
            return dir
        }
        if (!mounted) {
            throw Exception(context.getString(R.string.error_media_storage_not_mounted))
        }
        return null
    }

    private fun emptyProgress(userId: String, modifiedAt: String) = StoryProgressEntity(
        userId = userId,
        activeIssueId = null,
        activeUnlocked = 0,
        owedGrants = 0,
        completedIssueIds = emptyList(),
        readingLocale = ENGLISH,
        modifiedAt = modifiedAt,
    )

    companion object {
        const val ENGLISH = "en"
        private const val STORY_ART_DIR = "story_art"
    }
}

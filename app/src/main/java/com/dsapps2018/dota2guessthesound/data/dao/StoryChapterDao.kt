package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterEntity

@Dao
interface StoryChapterDao {

    @Query("SELECT modifiedAt FROM StoryChapter ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Query("SELECT COUNT(*) FROM StoryChapter WHERE issueId = :issueId")
    suspend fun countForIssue(issueId: Int): Int

    @Query(
        """
        SELECT COUNT(*) FROM StoryChapter
        WHERE issueId IN (SELECT id FROM StoryIssue WHERE isActive = 1)
        """
    )
    suspend fun countForActiveIssues(): Int

    @Query(
        "SELECT * FROM StoryChapter WHERE issueId = :issueId AND chapterIndex = :chapterIndex LIMIT 1"
    )
    suspend fun getByIssueAndIndex(issueId: Int, chapterIndex: Int): StoryChapterEntity?

    @Insert
    suspend fun insertAll(list: List<StoryChapterEntity>)

    @Delete
    suspend fun deleteAll(list: List<StoryChapterEntity>)

    @Query("DELETE FROM StoryChapter")
    suspend fun truncateTable()
}

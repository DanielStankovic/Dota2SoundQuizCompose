package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueI18nEntity

@Dao
interface StoryIssueI18nDao {

    @Query("SELECT modifiedAt FROM StoryIssueI18n WHERE locale = :locale ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(locale: String): String?

    @Query(
        "SELECT * FROM StoryIssueI18n WHERE issueId = :issueId AND locale = :locale LIMIT 1"
    )
    suspend fun get(issueId: Int, locale: String): StoryIssueI18nEntity?

    @Query(
        """
        SELECT COUNT(*) FROM StoryIssueI18n
        WHERE locale = :locale
          AND issueId IN (SELECT id FROM StoryIssue WHERE isActive = 1)
        """
    )
    suspend fun countForActiveIssues(locale: String): Int

    @Insert
    suspend fun insertAll(list: List<StoryIssueI18nEntity>)

    @Delete
    suspend fun deleteAll(list: List<StoryIssueI18nEntity>)

    @Query("DELETE FROM StoryIssueI18n")
    suspend fun truncateTable()
}

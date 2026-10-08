package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueRatingEntity

@Dao
interface StoryIssueRatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StoryIssueRatingEntity)

    @Query("SELECT * FROM StoryIssueRating WHERE issueId = :issueId LIMIT 1")
    suspend fun get(issueId: Int): StoryIssueRatingEntity?

    @Query("SELECT * FROM StoryIssueRating WHERE isSent = 0")
    suspend fun getUnsent(): List<StoryIssueRatingEntity>

    @Query("UPDATE StoryIssueRating SET isSent = 1 WHERE issueId = :issueId")
    suspend fun markSent(issueId: Int)

    @Query("DELETE FROM StoryIssueRating")
    suspend fun deleteAll()
}

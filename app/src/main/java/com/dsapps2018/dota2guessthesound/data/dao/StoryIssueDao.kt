package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueEntity

@Dao
interface StoryIssueDao {

    @Query("SELECT modifiedAt FROM StoryIssue ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Query("SELECT * FROM StoryIssue WHERE isActive = 1 ORDER BY sort")
    suspend fun getActive(): List<StoryIssueEntity>

    @Query("SELECT * FROM StoryIssue WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): StoryIssueEntity?

    @Insert
    suspend fun insertAll(list: List<StoryIssueEntity>)

    @Insert
    suspend fun insert(entity: StoryIssueEntity)

    @Delete
    suspend fun deleteAll(list: List<StoryIssueEntity>)

    @Query("DELETE FROM StoryIssue")
    suspend fun truncateTable()
}

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

    @Insert
    suspend fun insertAll(list: List<StoryIssueI18nEntity>)

    @Delete
    suspend fun deleteAll(list: List<StoryIssueI18nEntity>)

    @Query("DELETE FROM StoryIssueI18n")
    suspend fun truncateTable()
}

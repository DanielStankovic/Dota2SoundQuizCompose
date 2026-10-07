package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterI18nEntity

@Dao
interface StoryChapterI18nDao {

    @Query("SELECT modifiedAt FROM StoryChapterI18n WHERE locale = :locale ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(locale: String): String?

    @Query(
        "SELECT * FROM StoryChapterI18n WHERE chapterId = :chapterId AND locale = :locale LIMIT 1"
    )
    suspend fun get(chapterId: Int, locale: String): StoryChapterI18nEntity?

    @Insert
    suspend fun insertAll(list: List<StoryChapterI18nEntity>)

    @Delete
    suspend fun deleteAll(list: List<StoryChapterI18nEntity>)

    @Query("DELETE FROM StoryChapterI18n")
    suspend fun truncateTable()
}

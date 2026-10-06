package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryProgressEntity

@Dao
interface StoryProgressDao {

    @Query("SELECT * FROM StoryProgress WHERE userId = :userId")
    suspend fun get(userId: String): StoryProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StoryProgressEntity)

    @Query("DELETE FROM StoryProgress")
    suspend fun deleteAll()
}

package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.LevelClearTimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelClearTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LevelClearTimeEntity)

    @Query("SELECT * FROM LevelClearTime WHERE level = :level")
    suspend fun get(level: Int): LevelClearTimeEntity?

    @Query("SELECT * FROM LevelClearTime WHERE isSent = 0")
    suspend fun getUnsent(): List<LevelClearTimeEntity>

    @Query("SELECT * FROM LevelClearTime")
    fun observeAll(): Flow<List<LevelClearTimeEntity>>

    @Query("UPDATE LevelClearTime SET isSent = 1 WHERE level = :level")
    suspend fun markSent(level: Int)

    @Query("DELETE FROM LevelClearTime")
    suspend fun deleteAll()
}

package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.JourneyAssistUnlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyAssistUnlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: JourneyAssistUnlockEntity)

    @Query("SELECT * FROM JourneyAssistUnlock WHERE level = :level")
    suspend fun get(level: Int): JourneyAssistUnlockEntity?

    @Query("SELECT * FROM JourneyAssistUnlock WHERE level = :level")
    fun observe(level: Int): Flow<JourneyAssistUnlockEntity?>

    @Query("SELECT * FROM JourneyAssistUnlock WHERE isSent = 0")
    suspend fun getUnsent(): List<JourneyAssistUnlockEntity>

    @Query("SELECT * FROM JourneyAssistUnlock")
    fun observeAll(): Flow<List<JourneyAssistUnlockEntity>>

    @Query("UPDATE JourneyAssistUnlock SET isSent = 1 WHERE level = :level")
    suspend fun markSent(level: Int)

    @Query("DELETE FROM JourneyAssistUnlock")
    suspend fun deleteAll()
}

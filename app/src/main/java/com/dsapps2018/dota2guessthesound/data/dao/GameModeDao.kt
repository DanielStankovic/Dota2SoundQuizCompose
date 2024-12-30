package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.GameModeEntity

@Dao
interface GameModeDao {

    @Query("SELECT modifiedAt FROM GameMode ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Insert
    suspend fun insertAll(list: List<GameModeEntity>)

    @Insert
    suspend fun insert(gameModeEntity: GameModeEntity)

    @Delete
    suspend fun delete(gameModeEntity: GameModeEntity)

    @Delete
    suspend fun deleteAll(list: List<GameModeEntity>)

    @Query("SELECT id FROM GameMode WHERE code=:gameModeCode")
    suspend fun getGameModeIdByCode(gameModeCode: String): Int

}
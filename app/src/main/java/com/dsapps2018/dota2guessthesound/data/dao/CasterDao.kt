package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity

@Dao
interface CasterDao {

    @Query("SELECT modifiedAt FROM Caster ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Insert
    suspend fun insertAll(list: List<CasterEntity>)

    @Insert
    suspend fun insert(casterEntity: CasterEntity)

    @Delete
    suspend fun delete(casterEntity: CasterEntity)

    @Delete
    suspend fun deleteAll(list: List<CasterEntity>)

    @Query("DELETE FROM Caster")
    suspend fun truncateTable()

    @Query("SELECT name FROM Caster WHERE id IN (:heroIds) and isActive = 1")
    suspend fun getCasterNames(heroIds: List<Int>): List<String>

}
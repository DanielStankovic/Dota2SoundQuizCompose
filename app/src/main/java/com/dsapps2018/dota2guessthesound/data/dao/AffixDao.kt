package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.AffixEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AffixDao {

    @Query("SELECT modifiedAt FROM Affix ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Query("SELECT * FROM Affix ORDER BY id")
    suspend fun getAllAffixes(): List<AffixEntity>

    @Insert
    suspend fun insertAll(list: List<AffixEntity>)

    @Insert
    suspend fun insert(affixEntity: AffixEntity)

    @Delete
    suspend fun delete(affixEntity: AffixEntity)

    @Delete
    suspend fun deleteAll(list: List<AffixEntity>)

    @Query("DELETE FROM Affix")
    suspend fun truncateTable()
}
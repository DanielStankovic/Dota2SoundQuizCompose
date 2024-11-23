package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.ChangelogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChangelogDao {

    @Query("SELECT modifiedAt FROM Changelog ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Query("SELECT * FROM Changelog ORDER BY modifiedAt DESC")
    fun getAllChangelog(): Flow<List<ChangelogEntity>>

    @Insert
    suspend fun insertAll(list: List<ChangelogEntity>)

    @Insert
    suspend fun insert(changelogEntity: ChangelogEntity)

    @Delete
    suspend fun delete(changelogEntity: ChangelogEntity)

    @Delete
    suspend fun deleteAll(list: List<ChangelogEntity>)
}
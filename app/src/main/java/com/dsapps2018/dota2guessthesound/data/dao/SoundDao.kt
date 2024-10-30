package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundDao {

    @Query("SELECT modifiedAt FROM Sound ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Insert
    suspend fun insertAll(list: List<SoundEntity>)

    @Insert
    suspend fun insert(soundEntity: SoundEntity)

    @Delete
    suspend fun delete(soundEntity: SoundEntity)

    @Delete
    suspend fun deleteAll(list: List<SoundEntity>)

    @Query("SELECT id, spellName, soundFileLink FROM Sound WHERE isActive = 1")
    fun getAllSounds(): Flow<List<SoundModel>>

}
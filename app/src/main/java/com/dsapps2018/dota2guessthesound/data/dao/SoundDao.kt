package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity
import com.dsapps2018.dota2guessthesound.data.model.JourneySoundModel
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundDao {

    @Query("SELECT modifiedAt FROM Sound ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Transaction
    suspend fun insertAllTransaction(list: List<SoundEntity>) {
        list.chunked(50).forEach { chunk ->
            insertAll(chunk)
        }
    }

    @Insert
    suspend fun insertAll(list: List<SoundEntity>)

    @Insert
    suspend fun insert(soundEntity: SoundEntity)

    @Delete
    suspend fun delete(soundEntity: SoundEntity)

    @Delete
    suspend fun deleteAll(list: List<SoundEntity>)

    @Query("DELETE FROM Sound")
    suspend fun truncateTable()

    @Query("SELECT id, spellName, soundFileLink, isLocal FROM Sound WHERE isActive = 1")
    fun getAllSounds(): Flow<List<SoundModel>>

    @Query("SELECT s.id, s.spellName, s.soundFileLink, s.isLocal FROM Sound s INNER JOIN Caster c ON c.id = s.casterId WHERE c.name LIKE '%Invoker%' AND s.spellName NOT LIKE '%Invoke%'")
    fun getInvokerSounds(): Flow<List<SoundModel>>

    @Query("SELECT  s.id, s.spellName, s.soundFileLink, s.isLocal, CASE WHEN c.id IS NULL THEN 0 ELSE 1 END  AS isCorrectSound FROM Sound s LEFT JOIN Caster c ON c.id = s.casterId AND c.id IN (:heroIds) WHERE s.isActive = 1")
    suspend fun getJourneySounds(heroIds: List<Int>): List<JourneySoundModel>

}
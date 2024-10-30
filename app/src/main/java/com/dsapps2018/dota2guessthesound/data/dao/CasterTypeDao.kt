package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterTypeEntity

@Dao
interface CasterTypeDao {

    @Query("SELECT modifiedAt FROM CasterType ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Insert
    suspend fun insertAll(list: List<CasterTypeEntity>)

    @Insert
    suspend fun insert(casterTypeEntity: CasterTypeEntity)

    @Delete
    suspend fun delete(casterTypeEntity: CasterTypeEntity)

    @Delete
    suspend fun deleteAll(list: List<CasterTypeEntity>)

}
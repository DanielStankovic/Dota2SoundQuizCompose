package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dsapps2018.dota2guessthesound.data.db.entity.FaqEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaqDao {

    @Query("SELECT modifiedAt FROM Faq ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Query("SELECT * FROM Faq ORDER BY questionOrder")
    fun getAllFaqs(): Flow<List<FaqEntity>>

    @Insert
    suspend fun insertAll(list: List<FaqEntity>)

    @Insert
    suspend fun insert(faqEntity: FaqEntity)

    @Delete
    suspend fun delete(faqEntity: FaqEntity)

    @Delete
    suspend fun deleteAll(list: List<FaqEntity>)
}
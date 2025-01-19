package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardDetailsEntity

@Dao
interface LeaderboardDetailsDao {

    @Insert
    suspend fun insertDetails(leaderboardDetailsEntity: LeaderboardDetailsEntity)

    @Query("SELECT * FROM LeaderboardDetails WHERE isSent = 0 AND userId IS NOT NULL")
    suspend fun getAllUnsentDetails(): List<LeaderboardDetailsEntity>

    @Update
    suspend fun updateSentDetails(detailsList: List<LeaderboardDetailsEntity>)

    @Query("UPDATE LeaderboardDetails SET userId=:id WHERE userId IS NULL AND isSent = 0")
    suspend fun updateUserId(id: String)

    @Delete
    suspend fun deleteSentDetails(detailsList: List<LeaderboardDetailsEntity>)

    @Query("DELETE FROM LeaderboardDetails")
    suspend fun truncateTable()
}
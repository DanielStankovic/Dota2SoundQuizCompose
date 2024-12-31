package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardDetailsEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {

    @Query("SELECT modifiedAt FROM Leaderboard ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getModifiedDate(): String?

    @Insert
    suspend fun insert(leaderboardEntity: LeaderboardEntity)

    @Delete
    suspend fun delete(leaderboardEntity: LeaderboardEntity)

    @Query("DELETE FROM Leaderboard")
    suspend fun deleteAll()

    @Query("SELECT * FROM Leaderboard ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getLeaderboard(): LeaderboardEntity?

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

    @Query("SELECT startAt FROM Leaderboard ORDER BY modifiedAt DESC LIMIT 1")
    fun getLeaderboardStartDate(): Flow<String>

    @Query("SELECT id FROM Leaderboard ORDER BY modifiedAt DESC LIMIT 1")
    fun getLeaderboardId(): Flow<Int>
}
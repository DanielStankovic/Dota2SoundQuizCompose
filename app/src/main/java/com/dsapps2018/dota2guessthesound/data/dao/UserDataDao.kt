package com.dsapps2018.dota2guessthesound.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {

    @Insert
    suspend fun insert(userDataEntity: UserDataEntity)

    @Update
    suspend fun update(userDataEntity: UserDataEntity)

    @Query("SELECT * FROM UserData WHERE id=1")
    suspend fun getUserData(): UserDataEntity?

    @Query("SELECT * FROM UserData WHERE id=1")
    fun getUserDataFlow(): Flow<UserDataEntity>

}
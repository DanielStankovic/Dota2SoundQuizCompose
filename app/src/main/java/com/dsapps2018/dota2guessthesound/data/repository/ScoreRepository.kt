package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import javax.inject.Inject

class ScoreRepository @Inject constructor(
    private val userDataDao: UserDataDao
) {

    suspend fun getUserData() = userDataDao.getUserData()

    fun getUserDataFlow() = userDataDao.getUserDataFlow()

    suspend fun updateUserData(userDataEntity: UserDataEntity) = userDataDao.update(userDataEntity)
}
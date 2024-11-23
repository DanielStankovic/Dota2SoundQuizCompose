package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.dao.ChangelogDao
import com.dsapps2018.dota2guessthesound.data.db.entity.ChangelogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangelogRepository @Inject constructor(
    private val changelogDao: ChangelogDao
) {

    fun getAllChangelog(): Flow<List<ChangelogEntity>>{
        return changelogDao.getAllChangelog()
    }
}
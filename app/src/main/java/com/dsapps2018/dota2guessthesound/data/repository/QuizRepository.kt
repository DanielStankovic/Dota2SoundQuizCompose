package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val soundDao: SoundDao
) {

    fun getAllSounds(): Flow<List<SoundModel>>{
        return soundDao.getAllSounds().map { x ->
            x.shuffled()
        }
    }
}
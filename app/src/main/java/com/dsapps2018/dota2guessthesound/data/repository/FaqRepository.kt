package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.dao.FaqDao
import com.dsapps2018.dota2guessthesound.data.db.entity.FaqEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FaqRepository @Inject constructor(
    private val faqDao: FaqDao
) {

    fun getAllFaqs(): Flow<List<FaqEntity>>{
        return faqDao.getAllFaqs()
    }
}
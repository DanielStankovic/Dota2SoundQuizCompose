package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.JourneyDto
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.model.JourneySoundModel
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class JourneyRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val soundDao: SoundDao,
    private val casterDao: CasterDao
) {

    suspend fun getLevelData(levelNum: Int): JourneyDto {
        try {
            return postgrest
                .from(Constants.TABLE_JOURNEY)
                .select(
                    columns = Columns.list(
                        "id",
                        "level",
                        "radiant_heroes",
                        "dire_heroes",
                        "max_sounds",
                    )
                ) {
                    filter {
                        eq("level", levelNum)
                    }
                }.decodeSingle<JourneyDto>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getJourneySounds(heroIds: List<Int>): List<JourneySoundModel>{
        return soundDao.getJourneySounds(heroIds)
    }

    suspend fun getCasterNames(heroIds: List<Int>): List<String>{
        return casterDao.getCasterNames(heroIds)
    }

}
package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.JourneyLevelDto
import com.dsapps2018.dota2guessthesound.data.dao.AffixDao
import com.dsapps2018.dota2guessthesound.data.db.entity.AffixEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class JourneyLevelRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val affixDao: AffixDao
) {

    suspend fun getLevelsData(): List<JourneyLevelDto> {
        try {
            return postgrest.from(Constants.TABLE_JOURNEY)
                .select(
                    columns = Columns.list(
                        "id",
                        "level",
                        "affixes",
                    )
                ) {
                    order("level", Order.ASCENDING)
                }.decodeList<JourneyLevelDto>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAllAffixes(): List<AffixEntity>{
        return affixDao.getAllAffixes()
    }


}
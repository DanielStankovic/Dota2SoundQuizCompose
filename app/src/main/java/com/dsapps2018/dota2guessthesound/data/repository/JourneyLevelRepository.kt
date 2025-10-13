package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.JourneyLevelDto
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class JourneyLevelRepository @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getLevelsData(): List<JourneyLevelDto> {
        try {
            return postgrest.from(Constants.TABLE_JOURNEY)
                .select(
                    columns = Columns.list(
                        "id",
                        "level",
                    )
                ) {
                    order("level", Order.ASCENDING)
                }.decodeList<JourneyLevelDto>()
        } catch (e: Exception) {
            throw e
        }
    }

}
package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.ConfigDto
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    private val postgrest: Postgrest,
) {
    suspend fun getRemoteConfig(): ConfigDto {
        return try {

            postgrest.from(Constants.TABLE_CONFIG).select(
                Columns.raw(
                    """
            forced_version: data->forced_version,
            delete_version: data->delete_version,
            recommended_version: data->recommended_version
            """
                        .trimIndent()
                )
            ).decodeSingle<ConfigDto>()

        } catch (e: Exception) {
            throw e
        }
    }
}
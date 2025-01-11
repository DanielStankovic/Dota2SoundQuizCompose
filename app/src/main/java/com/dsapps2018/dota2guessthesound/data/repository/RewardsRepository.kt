package com.dsapps2018.dota2guessthesound.data.repository

import com.dsapps2018.dota2guessthesound.data.api.response.RewardDto
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class RewardsRepository @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getRewardsData(leaderboardId: Int): List<RewardDto> {
        try {
            return postgrest
                .from(Constants.TABLE_REWARDS)
                .select(
                    columns = Columns.list(
                        "image_url",
                        "item_image_url",
                        "item_url",
                        "item_name",
                        "standing",
                        "hero",
                        "item_type"
                    )
                ) {
                    filter {
                        eq("leaderboard_id", leaderboardId)
                    }
                }.decodeList<RewardDto>()
        } catch (e: Exception) {
            throw e
        }
    }
}
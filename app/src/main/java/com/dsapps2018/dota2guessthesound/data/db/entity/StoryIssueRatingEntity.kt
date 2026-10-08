package com.dsapps2018.dota2guessthesound.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local cache of the signed-in player's Issue Rating (1–5) plus dirty flag for RPC flush. */
@Entity(tableName = "StoryIssueRating")
data class StoryIssueRatingEntity(
    @PrimaryKey
    val issueId: Int,
    val stars: Int,
    val isSent: Boolean,
)

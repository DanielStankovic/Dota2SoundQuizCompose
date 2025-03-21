package com.dsapps2018.dota2guessthesound.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SyncDestination

@Serializable
object HomeDestination

@Serializable
object QuizModeDestination

@Serializable
data class FastFingerModeDestination(val time: Int)

@Serializable
object PickTimeDestination

@Serializable
object InvokerExplanationDestination

@Serializable
object InvokerDestination

@Serializable
object ForceUpdateDestination

@Serializable
data class PlayAgainDestination (val score: Int, val answeredAll: Boolean)

@Serializable
data class PlayAgainFastFingerDestination (val scoreGuessed:Int, val scoreTotal: Int, val time: Int, val answeredAll: Boolean)

@Serializable
data class PlayAgainInvokerDestination (val score: Int)

@Serializable
object OptionsDestination

@Serializable
object PrivacyDestination

@Serializable
object ChangelogDestination

@Serializable
object AttributionDestination

@Serializable
object ProfileDestination

@Serializable
data class LeaderboardDestination(val leaderboardId: Int? = null)

@Serializable
data class RewardsDestination(val leaderboardId: Int, val month:String)

@Serializable
object FaqDestination

@Serializable
object LeaderboardHistoryDestination
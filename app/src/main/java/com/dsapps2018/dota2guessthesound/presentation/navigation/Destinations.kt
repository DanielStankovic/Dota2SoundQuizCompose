package com.dsapps2018.dota2guessthesound.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SyncDestination

@Serializable
object LoginDestination

@Serializable
object SignUpDestination

@Serializable
object HomeDestination

@Serializable
object QuizModeDestination

@Serializable
object FastFingerModeDestination

@Serializable
object InvokerDestination

@Serializable
object ForceUpdateDestination

@Serializable
data class PlayAgainDestination (val score: Int, val answeredAll: Boolean)

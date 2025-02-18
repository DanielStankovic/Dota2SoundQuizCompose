package com.dsapps2018.dota2guessthesound.data.model

data class JourneyGameModel(
    val level: Int,
    val radiantHeroImages: List<Int>,
    val direHeroImages: List<Int>,
    val soundList: List<JourneySoundModel>,
    val totalCorrectSounds: Int
)

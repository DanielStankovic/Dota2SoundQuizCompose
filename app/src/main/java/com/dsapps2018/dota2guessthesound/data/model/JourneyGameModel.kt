package com.dsapps2018.dota2guessthesound.data.model

data class JourneyGameModel(
    val level: Int,
    val radiantHeroImages: List<Int>,
    /** Parallel to [radiantHeroImages]: true when that slot should use Affix blur. */
    val radiantHeroBlurred: List<Boolean> = emptyList(),
    val direHeroImages: List<Int>,
    /** Parallel to [direHeroImages]: true when that slot should use Affix blur. */
    val direHeroBlurred: List<Boolean> = emptyList(),
    val soundList: List<JourneySoundModel>,
    val totalCorrectSounds: Int
)

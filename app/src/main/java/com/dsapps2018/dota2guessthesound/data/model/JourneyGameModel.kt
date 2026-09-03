package com.dsapps2018.dota2guessthesound.data.model

import com.dsapps2018.dota2guessthesound.data.journey.HeroPortraitSlot

data class JourneyGameModel(
    val level: Int,
    val radiantHeroPortraits: List<HeroPortraitSlot>,
    val direHeroPortraits: List<HeroPortraitSlot> = emptyList(),
    val soundList: List<JourneySoundModel>,
    val totalCorrectSounds: Int
)

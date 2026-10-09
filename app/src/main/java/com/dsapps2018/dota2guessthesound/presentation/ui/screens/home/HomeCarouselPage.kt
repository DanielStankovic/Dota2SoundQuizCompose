package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import com.dsapps2018.dota2guessthesound.data.util.FeatureFlags

enum class HomeCarouselPage {
    Journey,
    HeroCatalog,
    Quiz,
    FastFinger,
    Invoker,
    Joke,
}

/**
 * Home carousel order: Journey first, then staging-only Hero Catalog, then arcade modes.
 */
fun homeCarouselPages(includeHeroCatalog: Boolean): List<HomeCarouselPage> = buildList {
    add(HomeCarouselPage.Journey)
    if (includeHeroCatalog) add(HomeCarouselPage.HeroCatalog)
    add(HomeCarouselPage.Quiz)
    add(HomeCarouselPage.FastFinger)
    add(HomeCarouselPage.Invoker)
    if (FeatureFlags.DAILY_JOKE_ENABLED) add(HomeCarouselPage.Joke)
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

enum class HomeCarouselPage {
    HeroCatalog,
    Quiz,
    FastFinger,
    Journey,
    Invoker,
    Joke,
}

fun homeCarouselPages(includeHeroCatalog: Boolean): List<HomeCarouselPage> = buildList {
    if (includeHeroCatalog) add(HomeCarouselPage.HeroCatalog)
    add(HomeCarouselPage.Quiz)
    add(HomeCarouselPage.FastFinger)
    add(HomeCarouselPage.Journey)
    add(HomeCarouselPage.Invoker)
    add(HomeCarouselPage.Joke)
}

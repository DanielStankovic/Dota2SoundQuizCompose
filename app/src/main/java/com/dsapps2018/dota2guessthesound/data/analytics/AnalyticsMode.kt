package com.dsapps2018.dota2guessthesound.data.analytics

/**
 * `mode` parameter values for product analytics. See docs/analytics-events.md.
 */
enum class AnalyticsMode(val id: String) {
    Quiz("quiz"),
    FastFinger("fast_finger"),
    FastFinger30("ff_30"),
    FastFinger60("ff_60"),
    FastFinger90("ff_90"),
    Invoker("invoker"),
    Journey("journey"),
    ;

    companion object {
        fun fastFinger(timeSeconds: Int): AnalyticsMode = when (timeSeconds) {
            60 -> FastFinger60
            90 -> FastFinger90
            else -> FastFinger30
        }
    }
}

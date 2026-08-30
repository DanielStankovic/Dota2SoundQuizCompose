package com.dsapps2018.dota2guessthesound.data.quiz

/**
 * One-shot rewarded continue for a classic Quiz run.
 * Journey Round owns its own continue gate (timer-coupled); see ADR-0003 / ADR-0007.
 */
class ExtraLifeGate {
    var used: Boolean = false
        private set

    fun canOfferContinue(): Boolean = !used

    fun markUsed() {
        used = true
    }

    fun reset() {
        used = false
    }
}

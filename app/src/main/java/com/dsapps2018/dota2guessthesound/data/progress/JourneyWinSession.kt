package com.dsapps2018.dota2guessthesound.data.progress

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the Journey win Mode Result across Issue Picker / Chapter reader → Mode Result chrome.
 * Cleared when the result screen binds it (or on a new first-clear gate).
 */
@Singleton
class JourneyWinSession @Inject constructor() {
    @Volatile
    var pending: JourneyWinResult? = null

    fun clear() {
        pending = null
    }
}

package com.dsapps2018.dota2guessthesound.data.journey

/**
 * Journey sound-board packing: always unique Decoys from Heroes not on the
 * roster, last row full. Column count is fixed so every device gets the same
 * board (Level Clear Time).
 */
object JourneySoundBoard {
    const val COLUMNS = 4
    const val MIN_DECOYS = 2

    /**
     * Target board size: at least [authoredMaxSounds], at least
     * [correctCount] + [MIN_DECOYS], rounded up to a multiple of [COLUMNS].
     */
    fun targetSize(authoredMaxSounds: Int, correctCount: Int): Int {
        val minWithDecoys = correctCount + MIN_DECOYS
        val desired = maxOf(authoredMaxSounds, minWithDecoys)
        val rem = desired % COLUMNS
        return if (rem == 0) desired else desired + (COLUMNS - rem)
    }
}

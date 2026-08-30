package com.dsapps2018.dota2guessthesound.data.quiz

/**
 * What happens after a wrong answer in a multiple-choice sound round.
 */
enum class MultipleChoiceWrongPolicy {
    /** Classic Quiz: stay on the failed sound until continue / game over. */
    StayOnWrong,

    /** Fast Finger: immediately deal the next sound. */
    AdvanceOnWrong,
}

sealed interface MultipleChoiceEvent {
    data class SoundReady(val buttonOptions: List<String>) : MultipleChoiceEvent
    data object Correct : MultipleChoiceEvent
    data object Wrong : MultipleChoiceEvent
    data object NoMoreSounds : MultipleChoiceEvent
    data object ConnectionLost : MultipleChoiceEvent
}

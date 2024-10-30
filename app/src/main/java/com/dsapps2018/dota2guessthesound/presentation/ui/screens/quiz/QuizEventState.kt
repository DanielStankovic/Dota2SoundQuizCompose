package com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz

sealed interface QuizEventState {
    data class SoundReady(val buttonOptions: List<String>) : QuizEventState
    data object CorrectSound: QuizEventState
    data object WrongSound: QuizEventState
    data object NoMoreSounds: QuizEventState
}
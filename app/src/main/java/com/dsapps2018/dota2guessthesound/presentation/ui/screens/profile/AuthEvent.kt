package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

sealed interface AuthEvent {
    data class Success (val message: String) : AuthEvent
    data class Error(val error: String, val isDismissible: Boolean = false) : AuthEvent
}
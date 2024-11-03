package com.dsapps2018.dota2guessthesound.data.util.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observe(): Flow<Status>

    fun isConnected(): Status

    enum class Status(val message: String) {
        Available("Back online"),
        Unavailable("No internet connection"),
        Losing("Internet connection lost"),
        Lost("No internet connection")
    }
}
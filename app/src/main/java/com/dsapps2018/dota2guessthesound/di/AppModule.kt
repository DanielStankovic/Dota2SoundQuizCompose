package com.dsapps2018.dota2guessthesound.di

import android.content.Context
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayer
import com.dsapps2018.dota2guessthesound.data.util.connectivity.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSoundPlayer(@ApplicationContext context: Context) =
        SoundPlayer(context = context)

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) =
        context.getSharedPreferences("Dota2SharedPrefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context) =
        NetworkConnectivityObserver(context)
}
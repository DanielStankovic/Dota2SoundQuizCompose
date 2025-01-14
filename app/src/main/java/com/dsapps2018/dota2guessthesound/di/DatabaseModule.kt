package com.dsapps2018.dota2guessthesound.di

import android.content.Context
import androidx.room.Room
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterTypeDao
import com.dsapps2018.dota2guessthesound.data.dao.ChangelogDao
import com.dsapps2018.dota2guessthesound.data.dao.FaqDao
import com.dsapps2018.dota2guessthesound.data.dao.GameModeDao
import com.dsapps2018.dota2guessthesound.data.dao.LeaderboardDetailsDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.DotaDatabase
import com.dsapps2018.dota2guessthesound.data.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context): DotaDatabase =
        Room.databaseBuilder(context, DotaDatabase::class.java, Constants.DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideCasterTypeDao(database: DotaDatabase): CasterTypeDao {
        return database.casterTypeDao()
    }

    @Singleton
    @Provides
    fun provideCasterDao(database: DotaDatabase): CasterDao {
        return database.casterDao()
    }

    @Singleton
    @Provides
    fun provideSoundDao(database: DotaDatabase): SoundDao {
        return database.soundDao()
    }

    @Singleton
    @Provides
    fun provideChangelogDao(database: DotaDatabase): ChangelogDao {
        return database.changelogDao()
    }

    @Singleton
    @Provides
    fun provideUserDataDao(database: DotaDatabase): UserDataDao {
        return database.userDataDao()
    }

    @Singleton
    @Provides
    fun provideGameModeDao(database: DotaDatabase): GameModeDao {
        return database.gameModeDao()
    }

    @Singleton
    @Provides
    fun provideLeaderboardDao(database: DotaDatabase): LeaderboardDetailsDao {
        return database.leaderboardDetailsDao()
    }

    @Singleton
    @Provides
    fun provideFaqDao(database: DotaDatabase): FaqDao {
        return database.faqDao()
    }

}
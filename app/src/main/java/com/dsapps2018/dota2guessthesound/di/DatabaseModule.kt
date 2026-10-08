package com.dsapps2018.dota2guessthesound.di

import android.content.Context
import androidx.room.Room
import com.dsapps2018.dota2guessthesound.data.dao.AffixDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterTypeDao
import com.dsapps2018.dota2guessthesound.data.dao.ChangelogDao
import com.dsapps2018.dota2guessthesound.data.dao.FaqDao
import com.dsapps2018.dota2guessthesound.data.dao.GameModeDao
import com.dsapps2018.dota2guessthesound.data.dao.LeaderboardDetailsDao
import com.dsapps2018.dota2guessthesound.data.dao.LevelClearTimeDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryChapterDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryChapterI18nDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueI18nDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryIssueRatingDao
import com.dsapps2018.dota2guessthesound.data.dao.StoryProgressDao
import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.DotaDatabase
import com.dsapps2018.dota2guessthesound.data.db.MIGRATION_5_6
import com.dsapps2018.dota2guessthesound.data.db.MIGRATION_6_7
import com.dsapps2018.dota2guessthesound.data.db.MIGRATION_7_8
import com.dsapps2018.dota2guessthesound.data.db.MIGRATION_8_9
import com.dsapps2018.dota2guessthesound.data.db.MIGRATION_9_10
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
        Room.databaseBuilder(context, DotaDatabase::class.java, Constants.DATABASE_NAME)
            .addMigrations(
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
            )
            .build()

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

    @Singleton
    @Provides
    fun provideAffixDao(database: DotaDatabase): AffixDao {
        return database.affixDao()
    }

    @Singleton
    @Provides
    fun provideLevelClearTimeDao(database: DotaDatabase): LevelClearTimeDao {
        return database.levelClearTimeDao()
    }

    @Singleton
    @Provides
    fun provideStoryIssueDao(database: DotaDatabase): StoryIssueDao {
        return database.storyIssueDao()
    }

    @Singleton
    @Provides
    fun provideStoryChapterDao(database: DotaDatabase): StoryChapterDao {
        return database.storyChapterDao()
    }

    @Singleton
    @Provides
    fun provideStoryIssueI18nDao(database: DotaDatabase): StoryIssueI18nDao {
        return database.storyIssueI18nDao()
    }

    @Singleton
    @Provides
    fun provideStoryChapterI18nDao(database: DotaDatabase): StoryChapterI18nDao {
        return database.storyChapterI18nDao()
    }

    @Singleton
    @Provides
    fun provideStoryProgressDao(database: DotaDatabase): StoryProgressDao {
        return database.storyProgressDao()
    }

    @Singleton
    @Provides
    fun provideStoryIssueRatingDao(database: DotaDatabase): StoryIssueRatingDao {
        return database.storyIssueRatingDao()
    }

}
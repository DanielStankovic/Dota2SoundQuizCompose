package com.dsapps2018.dota2guessthesound.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.dsapps2018.dota2guessthesound.data.dao.StoryProgressDao
import com.dsapps2018.dota2guessthesound.data.dao.UserDataDao
import com.dsapps2018.dota2guessthesound.data.db.entity.AffixEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterTypeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.ChangelogEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.FaqEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.GameModeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.LeaderboardDetailsEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.LevelClearTimeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryChapterI18nEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryIssueI18nEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.StoryProgressEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.UserDataEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `LevelClearTime` (" +
                "`level` INTEGER NOT NULL, " +
                "`clearTimeMs` INTEGER NOT NULL, " +
                "`isSent` INTEGER NOT NULL, " +
                "PRIMARY KEY(`level`))"
        )
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `StoryChapterI18n` ADD COLUMN `title` TEXT NOT NULL DEFAULT ''"
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `StoryIssue` (" +
                "`id` INTEGER NOT NULL, " +
                "`slug` TEXT NOT NULL, " +
                "`protagonistCasterId` INTEGER NOT NULL, " +
                "`sort` INTEGER NOT NULL, " +
                "`coverPath` TEXT NOT NULL, " +
                "`backdropPath` TEXT NOT NULL, " +
                "`coverFileLink` TEXT NOT NULL, " +
                "`backdropFileLink` TEXT NOT NULL, " +
                "`modifiedAt` TEXT NOT NULL, " +
                "`isActive` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `StoryChapter` (" +
                "`id` INTEGER NOT NULL, " +
                "`issueId` INTEGER NOT NULL, " +
                "`chapterIndex` INTEGER NOT NULL, " +
                "`modifiedAt` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `StoryIssueI18n` (" +
                "`issueId` INTEGER NOT NULL, " +
                "`locale` TEXT NOT NULL, " +
                "`title` TEXT NOT NULL, " +
                "`synopsis` TEXT NOT NULL, " +
                "`modifiedAt` TEXT NOT NULL, " +
                "PRIMARY KEY(`issueId`, `locale`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `StoryChapterI18n` (" +
                "`chapterId` INTEGER NOT NULL, " +
                "`locale` TEXT NOT NULL, " +
                "`body` TEXT NOT NULL, " +
                "`modifiedAt` TEXT NOT NULL, " +
                "PRIMARY KEY(`chapterId`, `locale`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `StoryProgress` (" +
                "`userId` TEXT NOT NULL, " +
                "`activeIssueId` INTEGER, " +
                "`activeUnlocked` INTEGER NOT NULL, " +
                "`owedGrants` INTEGER NOT NULL, " +
                "`completedIssueIds` TEXT NOT NULL, " +
                "`readingLocale` TEXT NOT NULL, " +
                "`modifiedAt` TEXT NOT NULL, " +
                "PRIMARY KEY(`userId`))"
        )
    }
}

@Database(
    entities = [
        CasterTypeEntity::class,
        CasterEntity::class,
        SoundEntity::class,
        ChangelogEntity::class,
        UserDataEntity::class,
        GameModeEntity::class,
        LeaderboardDetailsEntity::class,
        FaqEntity::class,
        AffixEntity::class,
        LevelClearTimeEntity::class,
        StoryIssueEntity::class,
        StoryChapterEntity::class,
        StoryIssueI18nEntity::class,
        StoryChapterI18nEntity::class,
        StoryProgressEntity::class,
    ],
    version = Constants.DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
    ],
    exportSchema = true
)
abstract class DotaDatabase : RoomDatabase() {

    abstract fun casterTypeDao(): CasterTypeDao

    abstract fun casterDao(): CasterDao

    abstract fun soundDao(): SoundDao

    abstract fun changelogDao(): ChangelogDao

    abstract fun userDataDao(): UserDataDao

    abstract fun gameModeDao(): GameModeDao

    abstract fun leaderboardDetailsDao(): LeaderboardDetailsDao

    abstract fun faqDao(): FaqDao

    abstract fun affixDao(): AffixDao

    abstract fun levelClearTimeDao(): LevelClearTimeDao

    abstract fun storyIssueDao(): StoryIssueDao

    abstract fun storyChapterDao(): StoryChapterDao

    abstract fun storyIssueI18nDao(): StoryIssueI18nDao

    abstract fun storyChapterI18nDao(): StoryChapterI18nDao

    abstract fun storyProgressDao(): StoryProgressDao

}

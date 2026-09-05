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

}

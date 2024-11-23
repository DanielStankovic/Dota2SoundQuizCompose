package com.dsapps2018.dota2guessthesound.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterTypeDao
import com.dsapps2018.dota2guessthesound.data.dao.ChangelogDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterTypeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.ChangelogEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants

@Database(
    entities = [CasterTypeEntity::class, CasterEntity::class, SoundEntity::class, ChangelogEntity::class],
    version = Constants.DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class DotaDatabase : RoomDatabase() {

    abstract fun casterTypeDao(): CasterTypeDao

    abstract fun casterDao(): CasterDao

    abstract fun soundDao(): SoundDao

    abstract fun changelogDao(): ChangelogDao

}
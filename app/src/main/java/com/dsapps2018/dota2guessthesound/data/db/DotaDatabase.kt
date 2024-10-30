package com.dsapps2018.dota2guessthesound.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterTypeDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterTypeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity

@Database(entities = [CasterTypeEntity::class, CasterEntity::class, SoundEntity::class], version = 1, exportSchema = false)
abstract class DotaDatabase : RoomDatabase() {

    abstract fun casterTypeDao(): CasterTypeDao

    abstract fun casterDao(): CasterDao

    abstract fun soundDao(): SoundDao
}
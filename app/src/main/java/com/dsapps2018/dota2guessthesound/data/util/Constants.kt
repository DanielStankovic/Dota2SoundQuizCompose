package com.dsapps2018.dota2guessthesound.data.util

object Constants {

    const val DATABASE_NAME = "dota_database"
    const val DATABASE_VERSION = 10

    /** Local mute for Chapter reader music bed (persists across Issues / Chapters). */
    const val PREF_STORY_MUSIC_MUTED = "story_music_muted"

    const val TABLE_CASTER_TYPE = "caster_type"
    const val TABLE_CASTERS = "casters"
    const val TABLE_GAME_MODE = "game_mode"
    const val TABLE_SOUNDS = "sounds"
    const val TABLE_CONFIG = "config"
    const val TABLE_CHANGELOG = "changelog"
    const val TABLE_LEADERBOARD = "leaderboard"
    const val TABLE_LEADERBOARD_DETAILS = "leaderboard_details"
    const val TABLE_LEADERBOARD_STANDING = "leaderboard_standing"
    const val TABLE_GAME_DATA = "game_data"
    const val TABLE_REWARDS = "rewards"
    const val TABLE_JOURNEY = "journey"
    const val TABLE_LEVEL_CLEAR_TIME = "level_clear_time"
    const val TABLE_FAQ = "faq"
    const val TABLE_AFFIXES = "affixes"
    const val TABLE_STORY_ISSUE = "story_issue"
    const val TABLE_STORY_CHAPTER = "story_chapter"
    const val TABLE_STORY_ISSUE_I18N = "story_issue_i18n"
    const val TABLE_STORY_CHAPTER_I18N = "story_chapter_i18n"
    const val TABLE_STORY_PROGRESS = "story_progress"
    const val TABLE_STORY_ISSUE_RATING = "story_issue_rating"

    const val DISCORD_INV_LINK = "https://discord.gg/2aH27NKEdA"
    const val DISCORD_PACKAGE_NAME = "com.discord"

    const val FF_30_SCORE_MIN = 3.0
    const val FF_60_SCORE_MIN = 8.0
    const val FF_90_SCORE_MIN = 18.0
    const val JOKE_COIN_COST = 1
    const val JOURNEY_FIRST_CLEAR_COINS = 20

    const val LAST_SYNC_KEY = "last_sync"

    const val PERMISSION_CHECK_TAG = "permissionCheck"
    const val FORCED_VERSION_TAG = "forcedVersionCheck"

    const val USER_AVATAR_URL_KEY = "avatar_url"

    const val TEN_DAYS_MILLIS = 10 * 24 * 60 * 60 * 1000L
    const val THREE_DAYS_MILLIS = 3 * 24 * 60 * 60 * 1000L

    const val EMPTY_STRING = ""

    const val BUCKET_NAME = "sounds"
    const val BUCKET_FOLDER_NAME = "spell_sounds"
    const val BUCKET_STORY_ART = "story_art"

    const val DOTA_VERSION = "7.41f"

    const val USER_DATA_ID = 1

}
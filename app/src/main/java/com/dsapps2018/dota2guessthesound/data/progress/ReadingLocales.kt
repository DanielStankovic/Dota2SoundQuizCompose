package com.dsapps2018.dota2guessthesound.data.progress

import java.util.Locale

/**
 * Curated Reading Locale codes for Issue copy (ADR-0019 / scripts/story-catalog/locales.py).
 * [JourneyIssue.ENGLISH] is the source locale and is always first in [all].
 */
object ReadingLocales {

    /** Target locales only (excludes English). Order matches the authoring catalog. */
    val targets: List<String> = listOf(
        "zh",
        "zh-Hant",
        "es",
        "pt",
        "hi",
        "ar",
        "bn",
        "ru",
        "ja",
        "pa",
        "id",
        "ur",
        "de",
        "fr",
        "ko",
        "tr",
        "vi",
        "sr-Latn",
        "fil",
        "uk",
        "th",
        "ro",
    )

    val all: List<String> = listOf(JourneyIssue.ENGLISH) + targets

    fun isCurated(code: String): Boolean = code in all

    /** Native display name for the picker (falls back to the code). */
    fun displayName(code: String): String {
        val locale = Locale.forLanguageTag(code)
        val name = locale.getDisplayName(locale).trim()
        if (name.isBlank() || name.equals(code, ignoreCase = true)) return code
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
}

package com.dsapps2018.dota2guessthesound.data.progress

/** What the first-clear path should show after Mode Result writes Issue Progress. */
sealed interface FirstClearOutcome {
    data object None : FirstClearOutcome
    data object ShowPicker : FirstClearOutcome
    data class ShowReader(
        val issueId: Int,
        val chapterIndex: Int,
        val unlockedCount: Int,
        val totalChapters: Int,
    ) : FirstClearOutcome
}

/** Result of picking an Issue while owed grants remain. */
sealed interface PickOutcome {
    data object ShowPickerAgain : PickOutcome
    data class ShowReader(
        val issueId: Int,
        val chapterIndex: Int,
        val unlockedCount: Int,
        val totalChapters: Int,
    ) : PickOutcome
}

data class IssuePickerItem(
    val issueId: Int,
    val title: String,
    val synopsis: String,
    val chapterCount: Int,
    val coverFilePath: String,
)

data class ChapterReaderPage(
    val issueId: Int,
    val chapterIndex: Int,
    val unlockedCount: Int,
    val totalChapters: Int,
    val body: String,
    val backdropFilePath: String,
    val issueTitle: String,
)

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

/** Public Issue Score for cards: average (one decimal) + vote count. */
data class IssueScore(
    val average: Float,
    val voteCount: Int,
) {
    companion object {
        val Empty = IssueScore(average = 0f, voteCount = 0)

        fun from(ratingSum: Long, ratingCount: Int): IssueScore {
            if (ratingCount <= 0) return Empty
            return IssueScore(
                average = ratingSum.toFloat() / ratingCount.toFloat(),
                voteCount = ratingCount,
            )
        }
    }
}

data class IssuePickerItem(
    val issueId: Int,
    val title: String,
    val synopsis: String,
    val chapterCount: Int,
    val coverFilePath: String,
    val score: IssueScore = IssueScore.Empty,
)

enum class IssueLibraryAccess {
    Active,
    Completed,
    /** Unstarted — cover/synopsis teaser only; cannot open the reader. */
    Locked,
}

/** Active, completed, and locked (unstarted) Issues for the Issue Library. */
data class IssueLibraryItem(
    val issueId: Int,
    val title: String,
    val synopsis: String,
    val chapterCount: Int,
    val unlockedCount: Int,
    val coverFilePath: String,
    val access: IssueLibraryAccess,
    val score: IssueScore = IssueScore.Empty,
) {
    /** Chapter to open: newest unlocked for Active, last Chapter when completed. */
    val openChapterIndex: Int
        get() = unlockedCount.coerceIn(1, chapterCount.coerceAtLeast(1))

    val canOpenReader: Boolean
        get() = access != IssueLibraryAccess.Locked && unlockedCount > 0
}

data class ChapterReaderPage(
    val issueId: Int,
    val chapterIndex: Int,
    val unlockedCount: Int,
    val totalChapters: Int,
    val body: String,
    val chapterTitle: String,
    val backdropFilePath: String,
    val issueTitle: String,
)

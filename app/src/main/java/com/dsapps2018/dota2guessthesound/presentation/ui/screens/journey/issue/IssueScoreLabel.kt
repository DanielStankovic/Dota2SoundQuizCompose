package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.IssueScore
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import java.util.Locale

/** Compact Issue Score chrome: `4.5 ★ (128)` — Maps-lite. */
@Composable
fun IssueScoreLabel(
    score: IssueScore,
    modifier: Modifier = Modifier,
) {
    val averageText = String.format(Locale.US, "%.1f", score.average)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = averageText,
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = JourneyTimeAccent,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.issue_score_votes, score.voteCount),
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

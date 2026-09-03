package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.data.journey.TimerState

@Composable
fun TimerDisplay(timerState: TimerState) {
    val seconds = (timerState.remainingMs / 1000).toInt()
    val progress = timerState.remainingMs.toFloat() / timerState.totalMs.toFloat()

    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = if (timerState.isPaused) "||" else "$seconds"

        // Longer values shrink to fit the fixed 40.dp chip.
        val fontSize = when {
            timerState.isPaused -> 20.sp
            text.length >= 3 -> 14.sp
            text.length == 2 -> 18.sp
            else -> 22.sp
        }

        CircularProgressIndicator(
            color = when {
                timerState.isPaused -> Color.Yellow.copy(alpha = 0.7f)
                timerState.isWarning -> Color.Red.copy(alpha = 0.7f)
                else -> Color.Cyan.copy(alpha = 0.7f)
            },
            modifier = Modifier.matchParentSize(),
            progress = { progress },
            strokeWidth = 3.dp
        )

        Text(
            text,
            fontSize = fontSize,
            color = if (timerState.isPaused) Color.Yellow else Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
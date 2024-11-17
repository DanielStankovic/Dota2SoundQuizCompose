package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressWithTimer(
    shouldShowTimer: Boolean,
    progress: Float,
    soundTimer: Int
) {
    if (shouldShowTimer) {
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.Yellow,
                modifier = Modifier.matchParentSize(),
                progress = {
                    progress
                },
                strokeWidth = 5.dp
            )
            Text(
                "${soundTimer}s",
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Box(modifier = Modifier.size(50.dp))
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.LeaderboardBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.LeaderboardOnBackground

@Composable
fun LeaderboardStandingComposable(
    leaderboardStanding: LeaderboardStandingDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (leaderboardStanding.isCurrentUser) LeaderboardOnBackground else LeaderboardBackground)
            .padding(12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leaderboardStanding.standing.toString().padEnd(4),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = if (leaderboardStanding.isCurrentUser) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(Modifier.width(16.dp))
        AsyncImage(
            model = leaderboardStanding.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            error = painterResource(R.drawable.ic_user)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = if (leaderboardStanding.isCurrentUser) "You"
            else
                leaderboardStanding.userName,
            color = Color.White,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            fontWeight = if (leaderboardStanding.isCurrentUser) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(Modifier.width(16.dp))
        Text(
            "${leaderboardStanding.score} pts",
            color = if (leaderboardStanding.isCurrentUser) Color.White else Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = if (leaderboardStanding.isCurrentUser) FontWeight.Bold else FontWeight.Normal
        )
    }
}
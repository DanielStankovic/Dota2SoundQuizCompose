package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.LeaderboardOnBackground


@Composable
fun TopStandingComposable(
    modifier: Modifier = Modifier,
    leaderboardStandingDto: LeaderboardStandingDto
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val maxBoxSize = (screenWidth.value / 3).dp - 16.dp
    val maxImageSize = (maxBoxSize.value * 0.9).dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(maxBoxSize)
                .offset(
                    y = if (leaderboardStandingDto.standing == 1) 0.dp else 20.dp
                )
                .scale(
                    when (leaderboardStandingDto.standing) {
                        1 -> 1f
                        2 -> 0.8f
                        3 -> 0.6f
                        else -> 1f
                    }
                )
        ) {
            // Circular Image
            AsyncImage(
                model = leaderboardStandingDto.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(maxImageSize)
                    .clip(CircleShape)
                    .border(
                        if (leaderboardStandingDto.isCurrentUser) 4.dp else 2.dp,
                        if (leaderboardStandingDto.isCurrentUser) DialogOnBackground else Color.Gray,
                        CircleShape
                    ),
                error = painterResource(R.drawable.ic_user)
            )

            // Circular Text Composable
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = (maxImageSize / 2).value.toInt().dp) // Half the size of the text composable to align properly
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        if (leaderboardStandingDto.isCurrentUser) 3.dp else 1.dp,
                        if (leaderboardStandingDto.isCurrentUser) DialogOnBackground else Color.Gray,
                        CircleShape
                    )
            ) {
                Text(
                    text = leaderboardStandingDto.standing.toString(),
                    color = Color.Black,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (leaderboardStandingDto.isCurrentUser) "You"
            else
                leaderboardStandingDto.userName,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = if (leaderboardStandingDto.isCurrentUser) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            "${leaderboardStandingDto.score} pts",
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }

}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LeaderboardStandingDto
import com.dsapps2018.dota2guessthesound.data.util.getMonthStringFromStringDate
import com.dsapps2018.dota2guessthesound.data.util.getMonthYearStringFromStringDate
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.LeaderboardBackground

@Composable
fun LeaderboardScreen(
    isHistory: Boolean,
    onHistoryClicked: () -> Unit,
    onQuestionClicked: () -> Unit,
    onCheckRewardClicked: (Int, String) -> Unit,
    leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
) {
    val timer by leaderboardViewModel.countdownFlow.collectAsStateWithLifecycle()
    val leaderboardState by leaderboardViewModel.leaderboardState.collectAsStateWithLifecycle()

    leaderboardViewModel.fetchLeaderboardStanding()
    Scaffold(contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LeaderboardBackground)
                    .padding(top = 70.dp)
            ) {

                LeaderboardContent({ timer },
                    leaderboardState = leaderboardState,
                    isHistory = isHistory,
                    onCheckRewardClicked = { leaderboardId, leaderboardMonth ->
                        onCheckRewardClicked(leaderboardId, leaderboardMonth)
                    },
                    onQuestionClicked = {
                        onQuestionClicked()
                    },
                    onHistoryClicked = {
                        onHistoryClicked()
                    },
                    onRefreshDataClicked = {
                        leaderboardViewModel.fetchLeaderboardStanding()
                    })
            }
        })
}

@Composable
fun LeaderboardContent(
    timer: () -> String,
    leaderboardState: LeaderboardViewModel.LeaderboardFetchState,
    isHistory: Boolean,
    onCheckRewardClicked: (Int, String) -> Unit,
    onQuestionClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onRefreshDataClicked: () -> Unit
) {
    when (leaderboardState) {
        is LeaderboardViewModel.LeaderboardFetchState.Error -> {
            ErrorOrEmptyContent(leaderboardState.error)
        }

        LeaderboardViewModel.LeaderboardFetchState.Loading -> {
            LoadingContent()
        }

        is LeaderboardViewModel.LeaderboardFetchState.Success -> {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isHistory) {
                        Icon(painter = painterResource(R.drawable.ic_question),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 4.dp)
                                .size(25.dp)
                                .clickable {
                                    onQuestionClicked()
                                })
                    }
                    Text(
                        modifier = Modifier.weight(1f), text = stringResource(
                            R.string.leaderboard_month_lbl, if (isHistory) {
                                getMonthYearStringFromStringDate(leaderboardState.leaderboardData.startAt)
                            } else {
                                getMonthStringFromStringDate(leaderboardState.leaderboardData.startAt)
                            }
                        ), fontSize = 18.sp, color = Color.White, textAlign = TextAlign.Center
                    )
                    if (!isHistory) {
                        Icon(painter = painterResource(R.drawable.ic_history),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(end = 20.dp, start = 4.dp)
                                .size(25.dp)
                                .clickable {
                                    onHistoryClicked()
                                })
                    }
                }
                LeaderboardData(
                    timer,
                    isHistory = isHistory,
                    leaderboardState.data,
                    onCheckRewardClicked = {
                        onCheckRewardClicked(
                            leaderboardState.leaderboardData.id,
                            getMonthStringFromStringDate(leaderboardState.leaderboardData.startAt)
                        )
                    },
                    onRefreshDataClicked = onRefreshDataClicked
                )

            }

        }
    }
}

@Composable
fun LeaderboardData(
    timer: () -> String,
    isHistory: Boolean,
    leaderboardStanding: List<LeaderboardStandingDto>,
    onCheckRewardClicked: () -> Unit,
    onRefreshDataClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerComposable {
                timer()
            }
            RewardComposable(onCheckRewardClicked = onCheckRewardClicked)
        }
        Spacer(Modifier.height(24.dp))
        if (leaderboardStanding.isEmpty()) {
            ErrorOrEmptyContent(stringResource(R.string.leaderboard_standing_no_data))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(color = Color.Black)
                    .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
            ) {
                if (!isHistory) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(stringResource(R.string.refresh_data),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                modifier = Modifier.clickable {
                                    onRefreshDataClicked()
                                })
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    item {
                        Spacer(Modifier.height(1.dp))
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        leaderboardStanding.getOrNull(1)?.let { it ->
                            TopStandingComposable(
                                modifier = Modifier.weight(1f), leaderboardStandingDto = it
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        leaderboardStanding.getOrNull(0)?.let { it ->
                            TopStandingComposable(
                                modifier = Modifier.weight(1f), leaderboardStandingDto = it
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        leaderboardStanding.getOrNull(2)?.let { it ->
                            TopStandingComposable(
                                modifier = Modifier.weight(1f), leaderboardStandingDto = it
                            )
                        }
                    }
                }
                if (leaderboardStanding.size > 3) {
                    items(
                        leaderboardStanding.subList(
                            3, leaderboardStanding.size
                        )
                    ) { leaderboardStanding ->
                        LeaderboardStandingComposable(leaderboardStanding)
                    }
                }
                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TimerComposable(
    timer: () -> String,
) {
    Text(
        timer(), color = Color.White, textAlign = TextAlign.Center, fontSize = 22.sp
    )
}

@Composable
fun RewardComposable(onCheckRewardClicked: () -> Unit) {
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp

    val infiniteTransition = rememberInfiniteTransition(label = "PulsatingButton")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsatingButton"
    )

    // Define the alpha animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.8f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsatingButton"
    )
    Image(painter = painterResource(R.drawable.check_rewards),
        contentDescription = null,
        modifier = Modifier
            .padding(end = 20.dp)
            .size((currentScreenWidth * 0.25).dp)
            .clickable {
                onCheckRewardClicked()
            }
            .scale(scale)
            .alpha(alpha))
}

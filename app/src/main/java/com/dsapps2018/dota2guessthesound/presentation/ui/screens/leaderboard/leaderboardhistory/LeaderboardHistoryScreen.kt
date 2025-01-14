package com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard.leaderboardhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.LeaderboardHistoryModel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.LeaderboardOnBackground

@Composable
fun LeaderboardHistoryScreen(
    onLeaderboardClicked: (Int) -> Unit,
    leaderboardHistoryViewModel: LeaderboardHistoryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val historyState by leaderboardHistoryViewModel.historyState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.home_background),
                        contentScale = ContentScale.Crop
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.leaderboard_history_lbl),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    HistoryContent(
                        historyState = historyState,
                        onLeaderboardClicked = onLeaderboardClicked
                    )
                }
            }
        }
    )
}

@Composable
fun HistoryContent(
    historyState: HistoryFetchState,
    onLeaderboardClicked: (Int) -> Unit
) {
    when (historyState) {
        HistoryFetchState.Loading -> {
            LoadingContent()
        }

        is HistoryFetchState.Error -> {
            ErrorOrEmptyContent(historyState.error)
        }

        is HistoryFetchState.Success -> {
            if (historyState.data.isEmpty()) {
                ErrorOrEmptyContent(stringResource(R.string.history_no_data))
            } else {
                HistoryData(
                    historyList = historyState.data,
                    onLeaderboardClicked = onLeaderboardClicked
                )
            }
        }
    }
}

@Composable
fun HistoryData(
    historyList: List<LeaderboardHistoryModel>,
    onLeaderboardClicked: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(historyList) { historyItem ->
            HistoryCard(
                historyItem = historyItem,
                onLeaderboardClicked = onLeaderboardClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryCard(
    historyItem: LeaderboardHistoryModel,
    onLeaderboardClicked: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onLeaderboardClicked(historyItem.id)
        },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = LeaderboardOnBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                historyItem.monthYearString,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}
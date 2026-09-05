package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.LevelClearTimeStandingDto
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevel

@Composable
fun LevelClearTimeScreen(
    modifier: Modifier = Modifier,
    viewModel: LevelClearTimeViewModel = hiltViewModel(),
) {
    val boardState by viewModel.boardState.collectAsStateWithLifecycle()
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                    )
                    .paint(
                        painterResource(R.drawable.journey_bg),
                        contentScale = ContentScale.Crop,
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_level_clear_time),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.level_clear_time_title,
                            viewModel.level,
                        ),
                        color = JourneyTimeAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clip(RoundedCornerShape(12.dp))
                            .background(JourneyButtonBackground.copy(alpha = 0.9f))
                            .clickable { viewModel.refresh() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh_data),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LevelClearTimeBoardContent(boardState)
                }
            }
        }
    )
}

@Composable
private fun LevelClearTimeBoardContent(
    state: LevelClearTimeViewModel.LevelClearTimeBoardState,
) {
    when (state) {
        LevelClearTimeViewModel.LevelClearTimeBoardState.Loading -> LoadingContent()
        LevelClearTimeViewModel.LevelClearTimeBoardState.Empty -> ErrorOrEmptyContent(
            stringResource(R.string.level_clear_time_empty)
        )
        is LevelClearTimeViewModel.LevelClearTimeBoardState.Error -> ErrorOrEmptyContent(
            state.message
        )
        is LevelClearTimeViewModel.LevelClearTimeBoardState.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(state.rows, key = { "${it.standing}-${it.isCurrentUser}" }) { row ->
                    LevelClearTimeRow(row)
                }
            }
        }
    }
}

@Composable
private fun LevelClearTimeRow(row: LevelClearTimeStandingDto) {
    val highlight = row.isCurrentUser
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (highlight) PlayLevel.copy(alpha = 0.45f)
                else JourneyButtonBackground.copy(alpha = 0.88f)
            )
            .border(
                width = if (highlight) 2.dp else 1.dp,
                color = if (highlight) JourneyTimeAccent else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = row.standing.toString(),
            color = JourneyTimeAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.width(8.dp))
        AsyncImage(
            model = row.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
            error = painterResource(R.drawable.ic_user),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (row.isCurrentUser) {
                stringResource(R.string.level_clear_time_you)
            } else {
                row.userName
            },
            color = Color.White,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = formatLevelClearTime(row.clearTimeMs),
            color = JourneyTimeAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

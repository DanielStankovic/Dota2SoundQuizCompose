package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import java.io.File

@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel = hiltViewModel(),
    onContinue: (level: Int, clearTimeMs: Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            when (val state = uiState) {
                ChapterReaderViewModel.ChapterReaderUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = JourneyTimeAccent)
                    }
                }
                is ChapterReaderViewModel.ChapterReaderUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = Color(0xFFFF8A80))
                            Spacer(Modifier.height(16.dp))
                            MenuButton(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(48.dp),
                                text = stringResource(R.string.chapter_reader_continue),
                                textColor = Color.White,
                                paddingValues = PaddingValues(),
                                contentScale = ContentScale.Fit,
                            ) {
                                onContinue(viewModel.level, viewModel.clearTimeMs)
                            }
                        }
                    }
                }
                is ChapterReaderViewModel.ChapterReaderUiState.Ready -> {
                    val page = state.page
                    val backdrop = page.backdropFilePath.takeIf { it.isNotBlank() }?.let { File(it) }
                    AsyncImage(
                        model = backdrop,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xCC0B1218),
                                        Color(0x990B1218),
                                        Color(0xE60B1218),
                                    )
                                )
                            )
                    )
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = page.issueTitle,
                            color = JourneyTimeAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.chapter_reader_progress,
                                page.chapterIndex,
                                page.totalChapters,
                            ),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = page.body,
                            color = Color.White,
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .background(
                                    Color.Black.copy(alpha = 0.25f),
                                    RoundedCornerShape(12.dp),
                                )
                                .padding(16.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextButton(
                                onClick = viewModel::previousChapter,
                                enabled = state.canGoPrevious,
                            ) {
                                Text(
                                    text = stringResource(R.string.chapter_reader_previous),
                                    color = if (state.canGoPrevious) {
                                        JourneyTimeAccent
                                    } else {
                                        Color.White.copy(alpha = 0.3f)
                                    },
                                )
                            }
                            TextButton(
                                onClick = viewModel::nextChapter,
                                enabled = state.canGoNext,
                            ) {
                                Text(
                                    text = stringResource(R.string.chapter_reader_next),
                                    color = if (state.canGoNext) {
                                        JourneyTimeAccent
                                    } else {
                                        Color.White.copy(alpha = 0.3f)
                                    },
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        MenuButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .align(Alignment.CenterHorizontally),
                            text = stringResource(R.string.chapter_reader_continue),
                            textColor = Color.White,
                            paddingValues = PaddingValues(),
                            contentScale = ContentScale.FillBounds,
                        ) {
                            onContinue(viewModel.level, viewModel.clearTimeMs)
                        }
                    }
                }
            }
        }
    }
}

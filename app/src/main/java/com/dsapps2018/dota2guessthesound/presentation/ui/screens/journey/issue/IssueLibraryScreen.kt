package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

@Composable
fun IssueLibraryScreen(
    viewModel: IssueLibraryViewModel = hiltViewModel(),
    onOpenReader: (issueId: Int, chapterIndex: Int, unlockedCount: Int) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B1218), Color(0xFF15202B), Color(0xFF0B1218))
                    )
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.issue_library_cd_back),
                        tint = JourneyTimeAccent,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onBack),
                    )
                    Spacer(Modifier.size(12.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_issue_library_book),
                        contentDescription = stringResource(R.string.issue_library_cd_book),
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.issue_library_title),
                        color = JourneyTimeAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.issue_library_subtitle),
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(18.dp))

                when (val state = uiState) {
                    IssueLibraryViewModel.IssueLibraryUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = JourneyTimeAccent)
                        }
                    }
                    IssueLibraryViewModel.IssueLibraryUiState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.issue_library_empty),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                            )
                        }
                    }
                    is IssueLibraryViewModel.IssueLibraryUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(state.message, color = Color(0xFFFF8A80), fontSize = 16.sp)
                        }
                    }
                    is IssueLibraryViewModel.IssueLibraryUiState.Ready -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                        ) {
                            items(state.items, key = { it.issueId }) { item ->
                                val canOpen = item.unlockedCount > 0
                                val badge = if (item.isActive) {
                                    stringResource(
                                        R.string.issue_library_badge_reading,
                                        item.unlockedCount,
                                        item.chapterCount,
                                    )
                                } else {
                                    stringResource(R.string.issue_library_badge_complete)
                                }
                                IssueStoryCard(
                                    title = item.title,
                                    synopsis = item.synopsis,
                                    coverFilePath = item.coverFilePath,
                                    metaLine = stringResource(
                                        R.string.issue_picker_chapter_count,
                                        item.chapterCount,
                                    ),
                                    badge = badge,
                                    enabled = canOpen,
                                    onClick = {
                                        if (!canOpen) return@IssueStoryCard
                                        onOpenReader(
                                            item.issueId,
                                            item.openChapterIndex,
                                            item.unlockedCount,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

@Composable
fun IssuePickerScreen(
    viewModel: IssuePickerViewModel = hiltViewModel(),
    onOpenReader: (
        level: Int,
        clearTimeMs: Long,
        issueId: Int,
        chapterIndex: Int,
        unlockedCount: Int,
    ) -> Unit,
    onNoIssues: (level: Int, clearTimeMs: Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nav by viewModel.nav.collectAsStateWithLifecycle()
    var expandedIssueId by remember { mutableStateOf<Int?>(null) }

    BackHandler(enabled = true) { /* must pick */ }

    LaunchedEffect(nav) {
        val destination = nav ?: return@LaunchedEffect
        when (destination) {
            IssuePickerViewModel.PickerNav.Result -> {
                onNoIssues(viewModel.level, viewModel.clearTimeMs)
                viewModel.consumeNav()
            }
            is IssuePickerViewModel.PickerNav.Reader -> {
                onOpenReader(
                    viewModel.level,
                    viewModel.clearTimeMs,
                    destination.issueId,
                    destination.chapterIndex,
                    destination.unlockedCount,
                )
                viewModel.consumeNav()
            }
        }
    }

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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.issue_picker_title),
                    color = JourneyTimeAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.issue_picker_subtitle),
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 15.sp,
                )
                Spacer(Modifier.height(20.dp))

                when (val state = uiState) {
                    IssuePickerViewModel.IssuePickerUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = JourneyTimeAccent)
                        }
                    }
                    IssuePickerViewModel.IssuePickerUiState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.issue_picker_empty),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                            )
                        }
                    }
                    is IssuePickerViewModel.IssuePickerUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(state.message, color = Color(0xFFFF8A80), fontSize = 16.sp)
                        }
                    }
                    is IssuePickerViewModel.IssuePickerUiState.Ready -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                        ) {
                            items(state.items, key = { it.issueId }) { item ->
                                IssueStoryCard(
                                    title = item.title,
                                    synopsis = item.synopsis,
                                    coverFilePath = item.coverFilePath,
                                    metaLine = stringResource(
                                        R.string.issue_picker_chapter_count,
                                        item.chapterCount,
                                    ),
                                    expanded = expandedIssueId == item.issueId,
                                    onToggleSynopsis = {
                                        expandedIssueId =
                                            if (expandedIssueId == item.issueId) null else item.issueId
                                    },
                                    onClick = { viewModel.pick(item.issueId) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

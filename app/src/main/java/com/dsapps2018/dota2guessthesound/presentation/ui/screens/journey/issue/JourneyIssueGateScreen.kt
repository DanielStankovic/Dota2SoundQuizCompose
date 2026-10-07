package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

@Composable
fun JourneyIssueGateScreen(
    viewModel: JourneyIssueGateViewModel = hiltViewModel(),
    onGoToResult: (level: Int, clearTimeMs: Long) -> Unit,
    onGoToPicker: (level: Int, clearTimeMs: Long) -> Unit,
    onGoToReader: (
        level: Int,
        clearTimeMs: Long,
        issueId: Int,
        chapterIndex: Int,
        unlockedCount: Int,
    ) -> Unit,
) {
    val route by viewModel.gateRoute.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.submitIfNeeded()
    }

    LaunchedEffect(route) {
        when (val r = route) {
            null -> Unit
            JourneyIssueGateViewModel.GateRoute.Result ->
                onGoToResult(viewModel.level, viewModel.clearTimeMs)
            JourneyIssueGateViewModel.GateRoute.Picker ->
                onGoToPicker(viewModel.level, viewModel.clearTimeMs)
            is JourneyIssueGateViewModel.GateRoute.Reader ->
                onGoToReader(
                    viewModel.level,
                    viewModel.clearTimeMs,
                    r.issueId,
                    r.chapterIndex,
                    r.unlockedCount,
                )
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = JourneyTimeAccent)
    }
}

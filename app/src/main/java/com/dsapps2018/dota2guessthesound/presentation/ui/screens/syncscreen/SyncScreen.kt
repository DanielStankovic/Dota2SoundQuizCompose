package com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.SingleOptionDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.ProgressColor

@Composable
fun SyncScreen(
    modifier: Modifier = Modifier,
    syncScreenViewModel: SyncScreenViewModel = hiltViewModel(),
    onSyncFinished: () -> Unit,
    onUpdateRequired: () -> Unit
) {
    var currentProgress by remember {
        mutableStateOf(ProgressUpdateEvent.ProgressUpdate(0f, 0f, Constants.EMPTY_STRING))
    }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        syncScreenViewModel.progressStatus.collect { progressStatusEvent ->
            when (progressStatusEvent) {
                is ProgressUpdateEvent.ProgressError -> {
                    showDialog = true
                }

                is ProgressUpdateEvent.ProgressUpdate -> {
                    currentProgress = progressStatusEvent
                }

                ProgressUpdateEvent.ProgressUpdateRequired -> {
                    onUpdateRequired()
                }

                ProgressUpdateEvent.SyncFinished -> {
                    onSyncFinished()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        content = { padding ->
            if (showDialog) SingleOptionDialog(
                onDismiss = {
                    showDialog = false
                },
                onOptionClick = {
                    syncScreenViewModel.restartSync()
                },
                titleText = context.getString(R.string.sync_error_title),
                messageText = context.getString(R.string.sync_error_message),
                optionText= context.getString(R.string.lbl_retry),
                dismissible = false
            )

            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.sync_background),
                        contentScale = ContentScale.Crop
                    ),

                ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${currentProgress.progressName}...",
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        trackColor = Color.Gray,
                        color = ProgressColor,
                        gapSize = (-15).dp,
                        progress = {
                            calculateProgress(currentProgress)
                        }
                    )

                    Spacer(modifier = Modifier.height(150.dp))
                }
            }
        }
    )
}

private fun calculateProgress(currentProgress: ProgressUpdateEvent.ProgressUpdate): Float {
    val progress = currentProgress.progress / currentProgress.maxProgress
    return if (!progress.isNaN()) {
        progress
    } else {
        0f
    }
}

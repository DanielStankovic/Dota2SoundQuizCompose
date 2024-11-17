package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ProgressWithTimer
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.SingleOptionDialog
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun InvokerScreen(
    modifier: Modifier = Modifier,
    invokerViewModel: InvokerViewModel = hiltViewModel(),
    onPlayAgain: (Int) -> Unit
) {

    val context = LocalContext.current
    var showConnectionDialog by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(0) }
    val isTimerRunning by invokerViewModel.isTimerRunning.collectAsStateWithLifecycle()
    val numOfHearts by invokerViewModel.numOfHearts.collectAsStateWithLifecycle()
    val orbList by invokerViewModel.orbList.collectAsStateWithLifecycle()
    val speedLevel by invokerViewModel.speedLevel.collectAsStateWithLifecycle()
    val soundTimer by invokerViewModel.soundTimer.collectAsStateWithLifecycle()
    val maxProgress by invokerViewModel.maxProgress.collectAsStateWithLifecycle()

    val animatedProgress by animateFloatAsState(
        targetValue = soundTimer / maxProgress.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Progress Animation"
    )

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1.seconds)
            time++
        }
    }

    LaunchedEffect(Unit) {
        invokerViewModel.quizEvent.collect { quizEvent ->
            when (quizEvent) {
                InvokerEventState.GameOver -> {
                    showInterstitial(context) {
                        onPlayAgain(time)
                    }
                }

                InvokerEventState.ConnectionLost -> {
                    showConnectionDialog = true
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        content = { padding ->

            if (showConnectionDialog) SingleOptionDialog(
                onDismiss = {
                    showConnectionDialog = false
                },
                onOptionClick = {
                    showConnectionDialog = false
                },
                titleText = context.getString(R.string.error_connection_lost_title),
                messageText = context.getString(R.string.error_connection_lost_msg),
                optionText = context.getString(R.string.lbl_ok),
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
                        painterResource(id = R.drawable.invoker_bg),
                        contentScale = ContentScale.Crop
                    ),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(70.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "${time}s",
                            fontSize = 30.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        repeat(numOfHearts) {
                            Image(
                                painterResource(R.drawable.ic_heart),
                                contentDescription = "",
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Speed Level\n${speedLevel}/6",
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        ProgressWithTimer(
                            shouldShowTimer = numOfHearts == 3,
                            progress = animatedProgress,
                            soundTimer = soundTimer
                        )
                        ProgressWithTimer(
                            shouldShowTimer = numOfHearts == 2,
                            progress = animatedProgress,
                            soundTimer = soundTimer
                        )
                        ProgressWithTimer(
                            shouldShowTimer = numOfHearts == 1,
                            progress = animatedProgress,
                            soundTimer = soundTimer
                        )

                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(R.drawable.sound_button),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clickable{
                            invokerViewModel.playSound()
                        },
                        contentScale = ContentScale.FillBounds
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        orbList.forEach { value ->
                            val imageRes = when (value) {
                                OrbType.QUAS -> R.drawable.ic_orb_quas
                                OrbType.WEX -> R.drawable.ic_orb_wex
                                OrbType.EXORT -> R.drawable.ic_orb_exort
                            }
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Row(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),

                        ) {
                        val imageModifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)

                        Image(
                            painterResource(R.drawable.ic_quas),
                            contentDescription = "",
                            modifier = imageModifier.clickable {
                                invokerViewModel.addElement(OrbType.QUAS)
                            },
                            contentScale = ContentScale.Crop
                        )

                        Image(
                            painterResource(R.drawable.ic_wex),
                            contentDescription = "",
                            modifier = imageModifier.clickable {
                                invokerViewModel.addElement(OrbType.WEX)
                            },
                            contentScale = ContentScale.Crop
                        )

                        Image(
                            painterResource(R.drawable.ic_exort),
                            contentDescription = "",
                            modifier = imageModifier.clickable {
                                invokerViewModel.addElement(OrbType.EXORT)
                            },
                            contentScale = ContentScale.Crop
                        )

                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,

                        ) {
                        Image(
                            painterResource(R.drawable.ic_invoke),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    invokerViewModel.checkAnswer()
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    )
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState
import com.dsapps2018.dota2guessthesound.data.journey.ExtraLifeContinueOffer
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundEvent
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundState
import com.dsapps2018.dota2guessthesound.data.journey.TimerState
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.WatchAdContinueDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables.TimerDisplay
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground

/** SB-A: min cell width so SoundCard mark targets stay ≥ 48.dp; Adaptive drops columns on narrow widths. */
private val SoundBoardMinCellSize: Dp = 72.dp

@Composable
fun JourneyGameScreen(
    modifier: Modifier = Modifier,
    viewModel: JourneyGameViewModel = hiltViewModel(),
    onNavigateToResultScreen: (Int, Boolean) -> Unit
) {

    val context = LocalContext.current
    val roundState by viewModel.roundState.collectAsStateWithLifecycle()
    val isRewardedReady by isAdReady.collectAsStateWithLifecycle()
    val levelNum = viewModel.levelNum
    val showWatchAdContinueDialog =
        (roundState as? JourneyRoundState.Ready)?.showContinueDialog == true
    val continueOffer =
        (roundState as? JourneyRoundState.Ready)?.continueOffer

    LaunchedEffect(Unit) {
        viewModel.gameEvent.collect { gameEvent ->
            when (gameEvent) {
                is JourneyRoundEvent.Correct -> {
                    onNavigateToResultScreen(levelNum, true)
                }

                is JourneyRoundEvent.GameOver -> {
                    onNavigateToResultScreen(levelNum, false)
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            if (showWatchAdContinueDialog) {
                if (isRewardedReady) {
                    val isTimeOffer =
                        continueOffer == ExtraLifeContinueOffer.TimeExtension
                    WatchAdContinueDialog(
                        onDismiss = {
                            viewModel.setShowWatchAdContinueDialog(false)
                        },
                        onSkipClicked = {
                            viewModel.setShowWatchAdContinueDialog(false)
                            showInterstitial(context) {
                                onNavigateToResultScreen(levelNum, false)
                            }
                        },
                        onWatchAdClicked = {
                            viewModel.setShowWatchAdContinueDialog(false)

                            showRewardedAd(context, onRewarded = {
                                viewModel.grantExtraLifeGate()
                            }, onAdDismissed = {
                                viewModel.resumeTimerAfterAd()
                            })
                        },
                        title = stringResource(
                            if (isTimeOffer) R.string.times_up_lbl else R.string.game_over_lbl
                        ),
                        message = stringResource(
                            if (isTimeOffer) {
                                R.string.watch_ad_for_more_time_msg
                            } else {
                                R.string.watch_add_to_continue_msg
                            }
                        ),
                    )
                } else {
                    viewModel.setShowWatchAdContinueDialog(false)
                    showInterstitial(context) {
                        onNavigateToResultScreen(levelNum, false)
                    }
                }
            }
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.journey_game_bg2),
                        contentScale = ContentScale.Crop
                    ),

                ) {
                JourneyGameContent(viewModel)
            }
        })
}

@Composable
fun JourneyGameContent(viewModel: JourneyGameViewModel) {
    val state by viewModel.roundState.collectAsStateWithLifecycle()
    when (val journeyState = state) {
        JourneyRoundState.Idle, JourneyRoundState.Loading -> {
            LoadingContent()
        }

        is JourneyRoundState.Error -> {
            ErrorOrEmptyContent(journeyState.message)
        }

        is JourneyRoundState.Ready -> {
            JourneyGameData(journeyState, viewModel)
        }
    }
}

@Composable
fun JourneyGameData(ready: JourneyRoundState.Ready, viewModel: JourneyGameViewModel) {
    val journeyState = ready.game
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val imageRowPadding = 8.dp
    val imageSize =
        ((currentScreenWidth - 2 * imageRowPadding.value) / journeyState.radiantHeroImages.size).coerceAtMost(
            150f
        )

    val affixUIState = ready.affixUI
    val timerState = ready.timer
    val showTimer = affixUIState.showTimer && timerState != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // One Affix status band: timer chip (when active) + hearts/counters/plays
        StatusInfoRow(
            hearts = ready.hearts,
            selectedMarks = ready.selectedMarks,
            remainingPlays = ready.remainingPlays,
            totalCorrectSounds = journeyState.totalCorrectSounds,
            affixUIState = affixUIState,
            timerState = if (showTimer) timerState else null
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = imageRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            journeyState.radiantHeroImages.forEachIndexed { index, img ->
                val shouldBlur = journeyState.radiantHeroBlurred.getOrElse(index) { false }
                val imageModifier = if (shouldBlur) {
                    Modifier
                        .size(imageSize.dp)
                        .blur(radius = affixUIState.blurIntensity.dp)
                } else {
                    Modifier.size(imageSize.dp)
                }

                Image(
                    painterResource(img),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = imageModifier,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = JourneyButtonBackground
            ),
            border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.7f)),
            modifier = Modifier
                .size(80.dp, 40.dp)
                .clickable {
                    viewModel.submitAnswer()
                }
                .shadow(
                    elevation = 30.dp,
                    ambientColor = Color.Blue,
                    spotColor = Color.Cyan,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            columns = GridCells.Adaptive(minSize = SoundBoardMinCellSize),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            items(items = journeyState.soundList, key = { it.soundModel.id }) { sound ->
                SoundCard(
                    selectedState = ready.selectedMarks.getValue(sound.soundModel.id),
                    onCardClicked = {
                        viewModel.toggleSoundCardState(sound.soundModel.id)
                    },
                    onSoundIconClicked = {
                        viewModel.playSound(sound.soundModel)
                    },
                    remainingPlays = ready.remainingPlays
                )
            }
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun StatusInfoRow(
    hearts: Int,
    selectedMarks: Map<Int, Boolean>,
    remainingPlays: Int?,
    totalCorrectSounds: Int,
    affixUIState: AffixUIState,
    timerState: TimerState? = null
) {
    val selectedSounds = selectedMarks.values.count { selected -> selected }
    val playsLeft = remainingPlays

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (timerState != null) {
            TimerDisplay(timerState = timerState)
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (affixUIState.showHearts) {
            repeat(hearts) {
                Image(
                    painterResource(R.drawable.ic_heart),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val markedSoundsString =
            if (affixUIState.showMarkedSoundCounter) selectedSounds.toString() else "?"
        val totalSoundsString =
            if (affixUIState.showSoundCounter) totalCorrectSounds.toString() else "?"

        Text(
            text = stringResource(
                R.string.sounds_selected_total,
                markedSoundsString,
                totalSoundsString
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        if (playsLeft != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Plays: $playsLeft",
                color = if (playsLeft <= 2) Color.Red else Color.Yellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

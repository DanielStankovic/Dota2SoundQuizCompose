package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.affix.AffixUIState
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundEvent
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundState
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.WatchAdContinueDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables.TimerDisplay
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground

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
                    WatchAdContinueDialog(onDismiss = {
                        viewModel.setShowWatchAdContinueDialog(false)
                    }, onSkipClicked = {
                        viewModel.setShowWatchAdContinueDialog(false)
                        showInterstitial(context) {
                            onNavigateToResultScreen(levelNum, false)
                        }
                    }, onWatchAdClicked = {
                        viewModel.setShowWatchAdContinueDialog(false)

                        showRewardedAd(context, onRewarded = {
                            viewModel.grantExtraLife()
                        }, onAdDismissed = {
                            viewModel.resumeTimerAfterAd()
                        })
                    })
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (affixUIState.showTimer && timerState != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                TimerDisplay(timerState = timerState)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        StatusInfoRow(
            hearts = ready.hearts,
            selectedMarks = ready.selectedMarks,
            remainingPlays = ready.remainingPlays,
            totalCorrectSounds = journeyState.totalCorrectSounds,
            affixUIState = affixUIState
        )

        if (affixUIState.showHeroImages) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = imageRowPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                journeyState.radiantHeroImages.forEach { img ->
                    val imageModifier = if (affixUIState.blurHeroImages) {
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
        } else {
            Text(
                text = "Heroes are hidden!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            columns = GridCells.Fixed(4),
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
    affixUIState: AffixUIState
) {
    val selectedSounds = selectedMarks.values.count { selected -> selected }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (affixUIState.showHearts) {
            repeat(hearts) {
                Image(
                    painterResource(R.drawable.ic_heart),
                    contentDescription = "",
                    modifier = Modifier.size(30.dp),
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
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        remainingPlays?.let { remaining ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Plays: $remaining",
                color = if (remaining <= 2) Color.Red else Color.Yellow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

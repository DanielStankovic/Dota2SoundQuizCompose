package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.journey.ExtraLifeContinueOffer
import com.dsapps2018.dota2guessthesound.data.journey.JourneyHudChrome
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundEvent
import com.dsapps2018.dota2guessthesound.data.journey.JourneyRoundState
import com.dsapps2018.dota2guessthesound.data.journey.TimerState
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.WatchAdContinueDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables.SoundquakeFxRequest
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables.TimerDisplay
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables.rememberSoundquakeFxState
import kotlin.random.Random
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
    var soundquakeFx by remember { mutableStateOf<SoundquakeFxRequest?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onHostPaused()
                Lifecycle.Event.ON_RESUME -> viewModel.onHostResumed()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.gameEvent.collect { gameEvent ->
            when (gameEvent) {
                is JourneyRoundEvent.Correct -> {
                    onNavigateToResultScreen(levelNum, true)
                }

                is JourneyRoundEvent.GameOver -> {
                    onNavigateToResultScreen(levelNum, false)
                }

                is JourneyRoundEvent.Soundquake -> {
                    soundquakeFx = SoundquakeFxRequest(
                        nonce = Random.nextLong(),
                        strong = gameEvent.strong,
                        drainedHeart = gameEvent.drainedHeart,
                    )
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
                            viewModel.onFullscreenAdStarted()
                            showRewardedAd(context, onRewarded = {
                                viewModel.grantExtraLifeGate()
                            }, onAdDismissed = {
                                viewModel.onFullscreenAdFinished()
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
                JourneyGameContent(viewModel, soundquakeFx)
            }
        })
}

@Composable
fun JourneyGameContent(
    viewModel: JourneyGameViewModel,
    soundquakeFx: SoundquakeFxRequest? = null,
) {
    val state by viewModel.roundState.collectAsStateWithLifecycle()
    when (val journeyState = state) {
        JourneyRoundState.Idle, JourneyRoundState.Loading -> {
            LoadingContent()
        }

        is JourneyRoundState.Error -> {
            ErrorOrEmptyContent(journeyState.message)
        }

        is JourneyRoundState.Ready -> {
            JourneyGameData(journeyState, viewModel, soundquakeFx)
        }
    }
}

@Composable
fun JourneyGameData(
    ready: JourneyRoundState.Ready,
    viewModel: JourneyGameViewModel,
    soundquakeFx: SoundquakeFxRequest? = null,
) {
    val journeyState = ready.game
    val radiantPortraits = journeyState.radiantHeroPortraits
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val imageRowPadding = 8.dp
    val imageSize =
        ((currentScreenWidth - 2 * imageRowPadding.value) /
            radiantPortraits.size.coerceAtLeast(1)).coerceAtMost(150f)

    val hud = ready.hud
    val timerState = ready.timer
    val showTimer = hud.showTimer && timerState != null
    val quakeFx = rememberSoundquakeFxState(soundquakeFx)

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 8.dp)
            .offset(x = quakeFx.shakeX.value.dp, y = quakeFx.shakeY.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // One Affix status band: timer chip (when active) + hearts/counters/plays
        StatusInfoRow(
            hearts = ready.hearts,
            selectedMarks = ready.selectedMarks,
            remainingPlays = ready.remainingPlays,
            totalCorrectSounds = journeyState.totalCorrectSounds,
            hud = hud,
            timerState = if (showTimer) timerState else null,
            heartScale = quakeFx.heartScale.value,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = imageRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            radiantPortraits.forEach { slot ->
                val imageModifier = if (slot.blurRadiusDp > 0f) {
                    Modifier
                        .size(imageSize.dp)
                        .blur(radius = slot.blurRadiusDp.dp)
                } else {
                    Modifier.size(imageSize.dp)
                }

                Image(
                    painterResource(slot.imageResId),
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
                    remainingPlays = ready.remainingPlays,
                    modifier = Modifier.animateItem(),
                )
            }
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }

        if (quakeFx.dustAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = quakeFx.dustAlpha.value))
            )
        }
    }
}

@Composable
fun StatusInfoRow(
    hearts: Int,
    selectedMarks: Map<Int, Boolean>,
    remainingPlays: Int?,
    totalCorrectSounds: Int,
    hud: JourneyHudChrome,
    timerState: TimerState? = null,
    heartScale: Float = 1f,
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

        if (hud.showHearts) {
            Row(
                modifier = Modifier.graphicsLayer {
                    scaleX = heartScale
                    scaleY = heartScale
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(hearts) {
                    Image(
                        painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val markedSoundsString =
            if (hud.showMarkedSoundCounter) selectedSounds.toString() else "?"
        val totalSoundsString =
            if (hud.showSoundCounter) totalCorrectSounds.toString() else "?"

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

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.journey.AffixListSlot
import com.dsapps2018.dota2guessthesound.data.journey.JourneyLevelAccess
import com.dsapps2018.dota2guessthesound.data.journey.JourneyLevelListRow
import com.dsapps2018.dota2guessthesound.data.journey.JourneyLevelsState
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.BannerView
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.board.formatLevelClearTime
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level.composables.AffixInfoBottomSheet
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun JourneyLevelScreen(
    modifier: Modifier = Modifier,
    levelViewModel: JourneyLevelViewModel = hiltViewModel(),
    onLevelClicked: (Int) -> Unit,
    onBoardClicked: (Int) -> Unit,
) {

    val journeyLevelState by levelViewModel.journeyLevelState.collectAsStateWithLifecycle()
    val journeyProgressText by levelViewModel.journeyProgressText.collectAsStateWithLifecycle()

    // State to track banner height
    var bannerHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
                    .paint(
                        painterResource(id = R.drawable.journey_bg),
                        contentScale = ContentScale.Crop
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    JourneyLevelTitle(
                        progressText = journeyProgressText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        JourneyLevelContent(
                            journeyLevelState,
                            levelViewModel,
                            bannerHeight,
                            onLevelClicked = { level ->
                                onLevelClicked(level)
                            },
                            onBoardClicked = onBoardClicked,
                        )
                    }
                }
                BannerView(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onHeightMeasured = { height ->
                        bannerHeight = height
                    }
                )
            }

        })
}

@Composable
fun JourneyLevelContent(
    state: JourneyLevelsState,
    viewModel: JourneyLevelViewModel,
    bannerHeight: Int,
    onLevelClicked: (Int) -> Unit,
    onBoardClicked: (Int) -> Unit,
) {
    when (state) {
        JourneyLevelsState.Loading -> {
            LoadingContent()
        }

        is JourneyLevelsState.Error -> {
            ErrorOrEmptyContent(state.message)
        }

        is JourneyLevelsState.Success -> {
            LevelData(
                levels = state.levels,
                totalItems = state.levels.size,
                levelViewModel = viewModel,
                bannerHeight = bannerHeight,
                onLevelClicked = onLevelClicked,
                onBoardClicked = onBoardClicked,
            )
        }
    }
}

@Composable
fun JourneyLevelTitle(
    progressText: String,
    modifier: Modifier = Modifier,
) {
    val titleShadow = Shadow(
        color = Color.Black.copy(alpha = 0.9f),
        offset = Offset(0f, 2f),
        blurRadius = 10f,
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = stringResource(R.string.hero_journey_lbl),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            style = TextStyle(shadow = titleShadow),
        )
        Text(
            text = progressText,
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            style = TextStyle(shadow = titleShadow),
        )
    }
}

@Composable
fun LevelData(
    levels: List<JourneyLevelListRow>,
    totalItems: Int,
    levelViewModel: JourneyLevelViewModel,
    bannerHeight: Int,
    onLevelClicked: (Int) -> Unit,
    onBoardClicked: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentLevel by levelViewModel.journeyLevel.collectAsStateWithLifecycle()
    val currentAffix by levelViewModel.currentAffix.collectAsStateWithLifecycle()
    val personalBests by levelViewModel.personalBests.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(initialPage = currentLevel) {
        totalItems
    }
    val isVisible by remember {
        derivedStateOf { pagerState.currentPage != currentLevel }
    }
    LaunchedEffect(pagerState.currentPage, currentLevel) {
        val viewed = levels.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
        if (viewed.affixSlots.none { it is AffixListSlot.Revealed }) {
            levelViewModel.setCurrentAffix(null)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState, contentPadding = PaddingValues(50.dp)
        ) {
            LevelCardContent(
                it,
                levels[it],
                pagerState,
                onItemClicked = onLevelClicked,
                onAffixIconClicked = { affix ->
                    levelViewModel.setCurrentAffix(affix)
                })
        }

        val viewedLevel = levels[pagerState.currentPage]
        val viewedCleared = viewedLevel.access == JourneyLevelAccess.Cleared
        val viewedBestMs = personalBests[viewedLevel.level]
        val cornerPad = (bannerHeight + 10).dp
        AnimatedVisibility(
            visible = viewedCleared,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = cornerPad, start = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_level_clear_time),
                    contentDescription = stringResource(R.string.level_clear_time_board_btn),
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onBoardClicked(viewedLevel.level) },
                )
                if (viewedBestMs != null) {
                    Text(
                        text = formatLevelClearTime(viewedBestMs),
                        color = JourneyTimeAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.9f),
                                offset = Offset(0f, 2f),
                                blurRadius = 8f,
                            )
                        ),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInHorizontally { it / 2 },
            exit = fadeOut() + slideOutHorizontally { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = (bannerHeight + 10).dp,
                    end = 10.dp
                )
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = RoundedCornerShape(15.dp))
                    .clickable {
                        scope.launch { pagerState.animateScrollToPage(currentLevel) }
                    }) {
                Image(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.ic_path),
                    contentDescription = null,
                )
            }
        }
    }

    currentAffix?.let { affix ->
        AffixInfoBottomSheet(
            affixName = affix.affix, affixDescription = affix.description, onDismiss = {
                levelViewModel.setCurrentAffix(null)
            })
    }
}

@Composable
fun LevelCardContent(
    index: Int,
    levelModel: JourneyLevelListRow,
    pagerState: PagerState,
    onItemClicked: (Int) -> Unit,
    onAffixIconClicked: (AffixModel) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Animation state for shake effect
    val shakeOffset = remember { Animatable(0f) }
    val isShaking = remember { mutableStateOf(false) }

    // Shake animation function
    suspend fun performShake() {
        if (isShaking.value) return // Prevent multiple shakes at once

        isShaking.value = true

        // Shake left and right
        repeat(3) {
            shakeOffset.animateTo(15f, tween(75))
            shakeOffset.animateTo(-15f, tween(75))
        }

        // Return to center
        shakeOffset.animateTo(0f, tween(75))
        isShaking.value = false

    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Level\n${levelModel.level}",
            style = TextStyle(
                shadow = Shadow(color = Color.Black, offset = Offset(x = 0f, y = 10f)),
                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(270.dp)
                .aspectRatio(0.67f)
                .padding(2.dp)
                .offset(x = shakeOffset.value.dp)
                .graphicsLayer {
                    val fraction = 1f - pagerState.offsetForPage(index).absoluteValue.coerceIn(0f, 1f)
                    val scale = lerp(0.85f, 1f, fraction)
                    scaleX = scale
                    scaleY = scale
                    alpha = lerp(0.7f, 1f, fraction)
                }
                .then(
                    if (levelModel.access == JourneyLevelAccess.Playable) {
                        Modifier.shadow(
                            elevation = 30.dp,
                            ambientColor = PlayLevel,
                            spotColor = PlayLevel,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable {
                    if (levelModel.access != JourneyLevelAccess.Locked) {
                        onItemClicked(levelModel.level)
                    } else {
                        scope.launch {
                            performShake()
                        }
                    }
                }

        ) {
            Image(
                painterResource(
                    when (levelModel.access) {
                        JourneyLevelAccess.Cleared -> R.drawable.level_replay_bg
                        JourneyLevelAccess.Playable -> R.drawable.level_play_bg
                        JourneyLevelAccess.Locked -> R.drawable.level_locked_bg
                    }
                ),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

        }
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            levelModel.affixSlots.forEach { slot ->
                when (slot) {
                    is AffixListSlot.Revealed -> LevelAffixToken(
                        painter = painterResource(slot.affix.iconResourceId),
                        contentDescription = null,
                        pagerState = pagerState,
                        index = index,
                        onClick = { onAffixIconClicked(slot.affix) },
                    )
                    AffixListSlot.Mystery -> LevelAffixToken(
                        painter = painterResource(R.drawable.affix_fog),
                        contentDescription = stringResource(R.string.affix_fog_cd),
                        pagerState = pagerState,
                        index = index,
                        onClick = null,
                    )
                }
            }
        }
    }

}

@Composable
private fun LevelAffixToken(
    painter: Painter,
    contentDescription: String?,
    pagerState: PagerState,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .graphicsLayer {
                alpha = 1f - pagerState.offsetForPage(index).absoluteValue
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
    )
}

private fun PagerState.offsetForPage(page: Int): Float =
    (currentPage - page) + currentPageOffsetFraction


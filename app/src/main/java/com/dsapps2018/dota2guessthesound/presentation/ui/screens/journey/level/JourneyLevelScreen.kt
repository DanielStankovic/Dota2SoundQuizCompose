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
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.JourneyLevelDto
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.data.model.JourneyLevelModel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.BannerView
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level.composables.AffixInfoBottomSheet
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevelTextGradient
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun JourneyLevelScreen(
    modifier: Modifier = Modifier,
    levelViewModel: JourneyLevelViewModel = hiltViewModel(),
    onLevelClicked: (Int) -> Unit
) {

    val journeyLevelState by levelViewModel.journeyLevelState.collectAsStateWithLifecycle()
    val journeyProgressText by levelViewModel.journeyProgressText.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        topBar = {
            JourneyLevelTopBar(
                progressText = journeyProgressText
            )
        },
        content = { padding ->
            Column(
                Modifier.padding(
                    bottom = padding.calculateBottomPadding(), top = padding.calculateTopPadding()
                )
            ) {
                Box(
                    modifier = modifier
                        .weight(1f)
                        .paint(
                            painterResource(id = R.drawable.journey_bg),
                            contentScale = ContentScale.FillBounds
                        )
                ) {
                    JourneyLevelContent(
                        journeyLevelState, levelViewModel, onLevelClicked = { level ->
                            onLevelClicked(level)
                        })
                }
                BannerView()
            }

        })
}

@Composable
fun JourneyLevelContent(
    state: JourneyLevelFetchState, viewModel: JourneyLevelViewModel, onLevelClicked: (Int) -> Unit
) {
    when (state) {
        JourneyLevelFetchState.Loading -> {
            LoadingContent()
        }

        is JourneyLevelFetchState.Error -> {
            ErrorOrEmptyContent(state.error)
        }

        is JourneyLevelFetchState.Success -> {
            LevelData(
                levels = state.data,
                totalItems = state.data.size,
                levelViewModel = viewModel,
                onLevelClicked = onLevelClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyLevelTopBar(
    progressText: String
) {
    TopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hero's Journey",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = progressText, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = JourneyButtonBackground.copy(alpha = 0.9f)
        )
    )
}

@Composable
fun LevelData(
    levels: List<JourneyLevelModel>,
    totalItems: Int,
    levelViewModel: JourneyLevelViewModel,
    onLevelClicked: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentLevel by levelViewModel.journeyLevel.collectAsStateWithLifecycle()
    val currentAffix by levelViewModel.currentAffix.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(initialPage = currentLevel) {
        totalItems
    }
    val isVisible by remember {
        derivedStateOf { pagerState.currentPage != currentLevel }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        HorizontalPager(
            state = pagerState, contentPadding = PaddingValues(50.dp)
        ) {
            LevelCardContent(
                it,
                levels[it],
                currentLevel,
                pagerState,
                onItemClicked = onLevelClicked,
                onAffixIconClicked = { affix ->
                    levelViewModel.setCurrentAffix(affix)
                })
        }
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInHorizontally { it / 2 },
            exit = fadeOut() + slideOutHorizontally { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp)
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
    levelModel: JourneyLevelModel,
    userCompletedLevel: Int,
    pagerState: PagerState,
    onItemClicked: (Int) -> Unit,
    onAffixIconClicked: (AffixModel) -> Unit
) {
    val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
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
                brush = Brush.linearGradient(
                    colors = PlayLevelTextGradient, start = Offset(0f, 0f), end = Offset(0f, 200f)
                ), fontWeight = FontWeight.Bold, fontSize = 30.sp
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
                    lerp(
                        start = 0.85f.dp,
                        stop = 1f.dp,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    alpha = lerp(
                        start = 0.7.dp,
                        stop = 1f.dp,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    ).value
                }
                .then(
                    if (levelModel.level == userCompletedLevel + 1) {
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
                    if (levelModel.level <= userCompletedLevel + 1) {
                        // Level is unlocked, perform normal click
                        onItemClicked(levelModel.level)
                    } else {
                        // Level is locked, perform shake animation
                        scope.launch {
                            performShake()
                        }
                    }
                }

        ) {
            Image(
                painterResource(
                    when {
                        levelModel.level <= userCompletedLevel -> R.drawable.level_replay_bg
                        levelModel.level == userCompletedLevel + 1 -> R.drawable.level_play_bg
                        else -> R.drawable.level_locked_bg
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
            levelModel.affixes.forEach { affix ->
                Image(
                    painter = painterResource(affix.iconResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .alpha(1f - pageOffset.absoluteValue)
                        .clickable {
                            onAffixIconClicked(affix)
                        })
            }
        }
    }

}

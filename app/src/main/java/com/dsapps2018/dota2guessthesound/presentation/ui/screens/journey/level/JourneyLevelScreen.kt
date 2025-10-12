package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.LevelModel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.BannerView
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevelTextGradient
import kotlin.math.absoluteValue

@Composable
fun JourneyLevelScreen(
    modifier: Modifier = Modifier,
    onLevelClicked: (Int) -> Unit,
    onBackClicked: () -> Unit = {},
    userCompletedLevel: Int = 5 // For now, assume user completed first 5 levels
) {
    val totalLevels = 100
    val currentLevel = userCompletedLevel + 1 // Next level to play
    val levelList = (1..100).map {
        LevelModel(it, emptyList())
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        topBar = {
            JourneyLevelTopBar(
                currentLevel = currentLevel,
                totalLevels = totalLevels
            )
        },
        content = { padding ->
            Column(
                Modifier.padding(
                    bottom = padding.calculateBottomPadding(),
                    top = padding.calculateTopPadding()
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

                    LevelData(
                        levels = levelList,
                        totalItems = levelList.size,
                        userCompletedLevel = 5
                    ) { level ->
                        onLevelClicked(level)
                    }
                }
                BannerView()
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyLevelTopBar(
    currentLevel: Int,
    totalLevels: Int
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
                    text = "Level $currentLevel of $totalLevels",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = JourneyButtonBackground.copy(alpha = 0.9f)
        )
    )
}

@Composable
fun LevelData(
    levels: List<LevelModel>,
    totalItems: Int,
    userCompletedLevel: Int = 5,
    onLevelClicked: (Int) -> Unit,
) {

    val pagerState = rememberPagerState(initialPage = userCompletedLevel) {
        totalItems
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(50.dp)
    ) {
        LevelCardContent(
            it,
            levels[it],
            userCompletedLevel,
            pagerState,
            onItemClicked = onLevelClicked
        )
    }
}

@Composable
fun LevelCardContent(
    index: Int,
    levelModel: LevelModel,
    userCompletedLevel: Int,
    pagerState: PagerState,
    onItemClicked: (Int) -> Unit
) {
    val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Level\n${levelModel.level}",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = PlayLevelTextGradient,
                    start = Offset(0f, 0f),
                    end = Offset(0f, 200f)
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
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
                .height(300.dp)
                .aspectRatio(0.67f)
                .padding(2.dp)
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

        ) {

            Image(
                painterResource(
                    when {
                        levelModel.level <= userCompletedLevel -> R.drawable.level_replay_bg
                        levelModel.level == userCompletedLevel + 1 -> R.drawable.level_play_bg
                        else -> R.drawable.level_locked_bg
                    }
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = levelModel.level <= userCompletedLevel + 1) {
                        onItemClicked(levelModel.level)
                    },
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

        }
    }

}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.rewards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.api.response.RewardDto
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.absoluteValue

@Composable
fun RewardsScreen(
    month: String,
    rewardsViewModel: RewardsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val rewardsState by rewardsViewModel.rewardsState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.rewards_background),
                        contentScale = ContentScale.Crop
                    )
            ) {
                RewardsContent(month, rewardsState)
            }
        }
    )


}

@Composable
fun RewardsContent(month: String, rewardsState: RewardsFetchState) {
    when (rewardsState) {
        RewardsFetchState.Loading -> {
            LoadingContent()
        }

        is RewardsFetchState.Error -> {
            ErrorOrEmptyContent(rewardsState.error)
        }

        is RewardsFetchState.Success -> {
            if (rewardsState.data.isEmpty()) {
                ErrorOrEmptyContent(stringResource(R.string.rewards_no_data))
            } else {
                RewardsData(month, rewardsState.data)
            }
        }
    }
}

@Composable
fun RewardsData(month: String, rewardList: List<RewardDto>) {
    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val currentScreenHeight = LocalConfiguration.current.screenHeightDp
    val uriHandler = LocalUriHandler.current

    val pagerState = rememberPagerState(initialPage = 1) {
        3
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            stringResource(R.string.rewards_month, month),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(50.dp),
            modifier = Modifier.weight(1f)
        ) {
            CardContent(it, rewardList[it].imageUrl, pagerState)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = (currentScreenHeight * 0.15).dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier.weight(1f),
                model = rewardList[pagerState.currentPage].itemImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(
                        R.string.hero_item_type,
                        rewardList[pagerState.currentPage].hero,
                        rewardList[pagerState.currentPage].itemType
                    ),
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    rewardList[pagerState.currentPage].itemName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.clickable {
                        uriHandler.openUri(rewardList[pagerState.currentPage].itemUrl)
                    }
                ) {
                    Text("View Online", color = Color.White, fontSize = 16.sp)
                    Icon(
                        painterResource(R.drawable.ic_export),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context, currentScreenWidth
                    ).height.dp
                )
        ) {
            AndroidView(
                // on below line specifying width for ads.
                modifier = Modifier.fillMaxWidth(), factory = { context ->
                    // on below line specifying ad view.
                    AdView(context).apply {
                        // on below line specifying ad size
                        setAdSize(
                            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                context, currentScreenWidth
                            )
                        )
                        // on below line specifying ad unit id
                        // currently added a test ad unit id.
                        adUnitId = context.getString(R.string.banner_id)
                        // calling load ad to load our ad.
                        loadAd(AdRequest.Builder().build())
                    }
                })
        }
    }
}

@Composable
fun CardContent(index: Int, imageUrl: String, pagerState: PagerState) {
    val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
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
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}
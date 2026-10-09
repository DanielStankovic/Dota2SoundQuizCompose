package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.loadRewardedAd
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrantStatus
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.CoinAdGrantCard
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.CoinAdGrantCardStyle
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@Composable
fun HomeInvokerComposable(
    currentIndex: Int,
    lastPageIndex: Int,
    animatedRotationY: Float,
    isRewardedReady: Boolean,
    canEnterInvoker: Boolean,
    coinAdGrant: CoinAdGrantStatus,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    setShowCoinInfoDialog: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onInvokerClicked: () -> Unit,
    onGrantCoinsFromAd: () -> Unit,
    onRefreshCoinAdGrant: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CoinAdGrantCard(
                grant = coinAdGrant,
                onWatchAdForCoins = {
                    if (isRewardedReady) {
                        showRewardedAd(
                            context,
                            onRewarded = onGrantCoinsFromAd,
                            onAdDismissed = {},
                        )
                    } else {
                        loadRewardedAd(context)
                    }
                },
                onRefreshGrantStatus = onRefreshCoinAdGrant,
                style = CoinAdGrantCardStyle.HomeOverlay,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArrowButton(false, currentIndex, lastPageIndex) {
                setShouldAnimate(true)
                setIsFlippingForward(false)
                onMoveLeft()
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                MenuButton(
                    modifier = Modifier
                        .wrapContentHeight()
                        .graphicsLayer {
                            rotationY = animatedRotationY
                            cameraDistance = 30f
                        }
                        .align(Alignment.Center),
                    paddingValues = PaddingValues(
                        horizontal = 30.dp
                    ),
                    enabled = canEnterInvoker,
                    maxLines = 1,
                    text = stringResource(R.string.invoker_lbl),
                    textColor = Color.White,
                    contentScale = ContentScale.Fit,
                ) {
                    onInvokerClicked()
                }
                Image(
                    painter = painterResource(R.drawable.coin_40),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 25.dp)
                        .offset(y = (-10).dp)
                        .size(40.dp)
                        .rotate(-45f)
                        .clickable {
                            setShowCoinInfoDialog(true)
                        }
                        .align(Alignment.TopStart)
                )
            }
            ArrowButton(true, currentIndex, lastPageIndex) {
                setShouldAnimate(true)
                setIsFlippingForward(true)
                onMoveRight()
            }
        }

        Box(modifier = Modifier.weight(0.6f))
    }
}

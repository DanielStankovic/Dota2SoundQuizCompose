package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@Composable
fun HomeInvokerComposable(
    currentIndex: Int,
    animatedRotationY: Float,
    isRewardedReady: Boolean,
    userCoinValue: Int,
    scale: Float,
    alpha: Float,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    setShowCoinInfoDialog: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onInvokerClicked: () -> Unit,
    updateCoinValue: (Int) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isRewardedReady) {
                Image(painter = painterResource(R.drawable.watch_video_btn),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            showRewardedAd(context, onRewarded = {
                                updateCoinValue(Constants.INVOKER_COIN_COST)
                            }, onAdDismissed = {})
                        }
                        .height(90.dp)
                        .width(200.dp)
                        .scale(scale)
                        .alpha(alpha))
            } else {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArrowButton(false, currentIndex) {
                setShouldAnimate(true)
                setIsFlippingForward(false)
                onMoveLeft()
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                MenuButton(modifier = Modifier
                    .wrapContentHeight()
                    .graphicsLayer {
                        rotationY = animatedRotationY
                        cameraDistance = 30f
                    }
                    .align(Alignment.Center),
                    paddingValues = PaddingValues(
                        horizontal = 30.dp
                    ),
                    enabled = userCoinValue >= Constants.INVOKER_COIN_COST,
                    maxLines = 1,
                    text = stringResource(R.string.invoker_lbl),
                    textColor = Color.White,
                    contentScale = ContentScale.Fit) {
                    onInvokerClicked()
                }
                Image(painter = painterResource(R.drawable.coin_200),
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
            ArrowButton(true, currentIndex) {
                setShouldAnimate(true)
                setIsFlippingForward(true)
                onMoveRight()
            }
        }

        Box(modifier = Modifier.weight(0.6f))
    }
}
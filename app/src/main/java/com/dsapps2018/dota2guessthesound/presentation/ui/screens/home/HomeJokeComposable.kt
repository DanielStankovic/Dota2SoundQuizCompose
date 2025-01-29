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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.dsapps2018.dota2guessthesound.data.util.toDp
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@Composable
fun HomeJokeComposable(
    currentIndex: Int,
    animatedRotationY: Float,
    isRewardedReady: Boolean,
    scale: Float,
    alpha: Float,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    setShowCoinInfoDialog: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onJokeClicked: () -> Unit,
    onJokeSoundClicked: () -> Unit,
    updateJokeCoinValue: (Int) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sound_button),
                contentDescription = null,
                modifier = Modifier
                    .size(100.toDp())
                    .offset(y = (-16).toDp())
                    .clickable{
                        onJokeSoundClicked()
                    }
            )
            Image(
                painter = painterResource(R.drawable.ogre_joke),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                contentScale = ContentScale.Fit
            )

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
                    enabled = false,
                    isComingSoonBtn = true,
                    maxLines = 1,
                    text = stringResource(R.string.daily_joke_lbl),
                    textColor = Color.White,
                    contentScale = ContentScale.Fit) {
                    onJokeClicked()
                }
                //TODO Za kasnije kad se ubaci logika
//                Image(painter = painterResource(R.drawable.joke_coin),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(start = 25.dp)
//                        .offset(y = (-10).dp)
//                        .size(40.dp)
//                        .rotate(-45f)
//                        .clickable {
//                            setShowCoinInfoDialog(true)
//                        }
//                        .align(Alignment.TopStart)
//
//                )
            }
            ArrowButton(true, currentIndex) {
                setShouldAnimate(true)
                setIsFlippingForward(true)
                onMoveRight()
            }
        }

        Box(modifier = Modifier.weight(0.6f), contentAlignment = Alignment.Center) {
            //TODO Otkomentarisati
//            if (isRewardedReady) {
//                Image(painter = painterResource(R.drawable.watch_ad_joke),
//                    contentDescription = null,
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 20.dp)
//                        .offset(y = 20.dp)
//                        .clickable {
//                            showRewardedAd(context, onRewarded = {
//                                updateJokeCoinValue(Constants.JOKE_COIN_COST)
//                            }, onAdDismissed = {})
//                        }
//                        .scale(scale)
//                        .alpha(alpha))
//            } else {
//                Spacer(modifier = Modifier.fillMaxSize().padding(top = 20.dp))
//            }
        }
    }
}
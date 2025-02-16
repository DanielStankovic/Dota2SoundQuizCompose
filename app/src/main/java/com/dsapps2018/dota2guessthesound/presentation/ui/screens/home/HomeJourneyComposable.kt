package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeJourneyComposable(
    currentIndex: Int,
    isLoggedIn: Boolean,
    animatedRotationY: Float,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onJourneyClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {

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
                    enabled = isLoggedIn,
                    maxLines = 1,
                    text = stringResource(R.string.hero_journey_lbl),
                    textColor = Color.White,
                    contentScale = ContentScale.Fit) {
                    onJourneyClicked()
                }
                if(!isLoggedIn) {
                    Image(painter = painterResource(R.drawable.login_required_btn),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 25.dp)
                            .offset(y = (-15).dp)
                            .size(60.dp)
                            .rotate(-45f)
                            .align(Alignment.TopStart)
                    )
                }
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
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.SpeechBubble

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeFastFingerComposable(
    currentIndex: Int,
    animatedRotationY: Float,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onFastFingerClicked: () -> Unit,
    onMoveToJokeScreen: () -> Unit
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
            SpeechBubble(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp),
                rowHeight = maxHeight,
                text = "Psst!\nWanna hear a Cringe Joke?\n" +
                        "Click here!",
                res = R.drawable.kotl
            ) {
                onMoveToJokeScreen()
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
            MenuButton(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .graphicsLayer {
                        rotationY = animatedRotationY
                        cameraDistance = 30f

                    },
                paddingValues = PaddingValues(
                    horizontal = 30.dp,
                ),
                maxLines = 1,
                text = stringResource(R.string.fast_finger_lbl),
                textColor = Color.White,
                contentScale = ContentScale.Fit,
            ) {
                onFastFingerClicked()
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
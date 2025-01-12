package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.dsapps2018.dota2guessthesound.data.util.toDp
import kotlinx.coroutines.launch

@Composable
fun AnimatedImages(
    modifier: Modifier,
    bigImage: Painter,
    smallImageCorrect: Painter,
    smallImageWrong: Painter,
    animationTriggerCorrect: Boolean,
    resetAnimationTriggerCorrect: () -> Unit,
    animationTriggerWrong: Boolean = false,
    resetAnimationTriggerWrong: () -> Unit = {},
    onImageClick: () -> Unit,
    floatOffsetCorrect: Float
) {
    val scope = rememberCoroutineScope()

    // Animatable for handling the x-axis offset
    val offsetX = remember { Animatable(0f) }

    // Animatable for handling the y-axis offset
    val offsetY = remember { Animatable(0f) }


    LaunchedEffect(key1 = animationTriggerCorrect) {
        // Check if animation is triggered
        if (animationTriggerCorrect) {
            scope.launch {
                // Start the animation
                offsetX.animateTo(
                    targetValue = floatOffsetCorrect, // Move left by 100.dp
                    animationSpec = tween(durationMillis = 1000) // Duration of 1.5 seconds
                )
                offsetX.animateTo(
                    targetValue = 0f, // Return to original position
                    animationSpec = tween(durationMillis = 1000)
                )

                // Reset the animation trigger in ViewModel
                resetAnimationTriggerCorrect()
            }
        }
    }

    LaunchedEffect(key1 = animationTriggerWrong) {
        // Check if animation is triggered
        if (animationTriggerWrong) {
            scope.launch {
                // Start the animation
                offsetY.animateTo(
                    targetValue = -360f, // Move up by 100.dp
                    animationSpec = tween(durationMillis = 1000) // Duration of 1.5 seconds
                )
                offsetY.animateTo(
                    targetValue = 0f, // Return to original position
                    animationSpec = tween(durationMillis = 1000)
                )

                // Reset the animation trigger in ViewModel
                resetAnimationTriggerWrong()
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Small image correct behind (initially offset to the right)
        Image(
            painter = smallImageCorrect,
            contentDescription = "Small Image Correct",
            modifier = Modifier
                .size(400.toDp())
                .offset(x = offsetX.value.toDp())
                .background(Color.Transparent),
            contentScale = ContentScale.Inside
        )

        // Small image wrong behind (initially offset to the bottom)
        Image(
            painter = smallImageWrong,
            contentDescription = "Small Image Wrong",
            modifier = Modifier
                .size(400.toDp())
                .offset(y = offsetY.value.toDp())
                .background(Color.Transparent),
            contentScale = ContentScale.Inside
        )

        // Big image in front
        Image(
            painter = bigImage,
            contentDescription = "Big Image",
            modifier = Modifier
                .matchParentSize()
                .clickable{
                    onImageClick()
                },
            contentScale = ContentScale.FillBounds
        )
    }
}
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimatedImages(
    modifier: Modifier,
    bigImage: Painter,
    smallImage: Painter,
    animationTrigger: Boolean,
    resetAnimationTrigger: () -> Unit,
    onImageClick: () -> Unit,
    floatOffset: Float
) {
    val scope = rememberCoroutineScope()

    // Animatable for handling the x-axis offset
    val offsetX = remember { Animatable(0f) }


    LaunchedEffect(key1 = animationTrigger) {
        // Check if animation is triggered
        if (animationTrigger) {
            scope.launch {
                // Start the animation
                offsetX.animateTo(
                    targetValue = floatOffset, // Move left by 100.dp
                    animationSpec = tween(durationMillis = 1000) // Duration of 1.5 seconds
                )
                offsetX.animateTo(
                    targetValue = 0f, // Return to original position
                    animationSpec = tween(durationMillis = 1000)
                )

                // Reset the animation trigger in ViewModel
                resetAnimationTrigger()
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Small image behind (initially offset to the right)
        Image(
            painter = smallImage,
            contentDescription = "Small Image",
            modifier = Modifier
                .size(150.dp)
                .offset(x = offsetX.value.dp)
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
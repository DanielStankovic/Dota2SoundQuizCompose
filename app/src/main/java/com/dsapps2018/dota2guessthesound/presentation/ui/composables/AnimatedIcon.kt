package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R

@Composable
fun AnimatedIcon(
    index: Int,
    modifier: Modifier = Modifier
) {
    // Alpha will be animated between 0 and 1
    val alphaAnimation = remember {
        Animatable(initialValue = 1f)
    }

    // Remember which icon was showing
    var currentIcon by remember {
        mutableStateOf(index == 3)
    }

    // Launch animation when index changes
    LaunchedEffect(index) {
        if(index == 0 || index == 1) return@LaunchedEffect
        if((index == 2 && !currentIcon)) return@LaunchedEffect

        // Fade out
        alphaAnimation.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 150)
        )

        // Change icon
        currentIcon = index == 3

        // Fade in
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 150)
        )
    }

    Image(painter = painterResource(if(currentIcon)R.drawable.joke_coin else R.drawable.coin_blank),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clickable {
                //setShowCoinInfoDialog(true)
            }.graphicsLayer {
                alpha = alphaAnimation.value
            }
    )
}
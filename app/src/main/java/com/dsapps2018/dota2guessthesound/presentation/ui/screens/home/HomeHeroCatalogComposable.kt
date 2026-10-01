package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun HomeHeroCatalogComposable(
    currentIndex: Int,
    lastPageIndex: Int,
    animatedRotationY: Float,
    setShouldAnimate: (Boolean) -> Unit,
    setIsFlippingForward: (Boolean) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onHeroCatalogClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrowButton(false, currentIndex, lastPageIndex) {
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
                paddingValues = PaddingValues(horizontal = 30.dp),
                maxLines = 2,
                text = stringResource(R.string.hero_catalog_lbl),
                textColor = Color.White,
                contentScale = ContentScale.Fit,
            ) {
                onHeroCatalogClicked()
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

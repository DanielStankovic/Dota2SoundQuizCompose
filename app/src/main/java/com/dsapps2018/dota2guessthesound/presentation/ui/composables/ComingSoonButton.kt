package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComingSoonButton(
    text: String,
    textColor: Color,
    enabled: Boolean = true,
    @DrawableRes resId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(
                if (enabled) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                }
            )
            .paint(
                painterResource(id = resId),
                contentScale = ContentScale.Fit
            )
            .padding(horizontal = 40.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 22.sp, color = textColor)
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
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
import com.dsapps2018.dota2guessthesound.R

@Composable
fun InvokerButton(
    text: String,
    textColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    BoxWithConstraints {
        val boxWithConstraintsScope = this
        val xOffset = 0.2 * boxWithConstraintsScope.maxWidth.value
        val yOffset = 0.01 * boxWithConstraintsScope.maxHeight.value
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
                    painterResource(id = if (enabled) R.drawable.button_bg else R.drawable.button_disabled_bg),
                    contentScale = ContentScale.Fit
                )
                .padding(horizontal = 40.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 22.sp, color = textColor)
            Icon(
                modifier = Modifier.align(Alignment.TopEnd).offset(x = xOffset.dp, y = -yOffset.dp),
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite Icon",
                tint = Color.Red
            )
        }
    }
}
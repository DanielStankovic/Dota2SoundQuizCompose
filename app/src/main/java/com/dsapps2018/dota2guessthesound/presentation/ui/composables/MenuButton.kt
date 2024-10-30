package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R

@Composable
fun MenuButton(
    modifier: Modifier,
    text: String,
    textColor: Color,
    paddingValues: PaddingValues,
    enabled: Boolean = true,
    contentScale: ContentScale,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
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
                contentScale = contentScale
            )
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 22.sp, color = textColor, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
    }
}
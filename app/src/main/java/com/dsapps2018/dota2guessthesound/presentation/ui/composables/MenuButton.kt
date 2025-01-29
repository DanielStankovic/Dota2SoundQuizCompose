package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
fun MenuButton(
    modifier: Modifier,
    text: String,
    textColor: Color,
    paddingValues: PaddingValues,
    enabled: Boolean = true,
    isComingSoonBtn: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
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
            .padding(paddingValues)
            .paint(
                painterResource(id = getButtonImageId(isComingSoonBtn, enabled)),
                contentScale = contentScale
            ),
        contentAlignment = Alignment.Center
    ) {
        AutoSizeText(
            text = text,
            maxTextSize = 22.sp,
            minTextSize = 12.sp,
            color = textColor,
            maxLines = maxLines,
            alignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

private fun getButtonImageId(isComingSoon: Boolean, enabled: Boolean): Int {
        if(isComingSoon) return R.drawable.fast_finger_coming_soon
        return if(enabled) R.drawable.button_bg else R.drawable.button_disabled_bg
}
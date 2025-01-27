package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R

@Composable
fun SpeechBubble(
    modifier: Modifier,
    rowHeight: Dp,
    text: String,
    @DrawableRes res: Int,
    onClick: () -> Unit
) {
    val imageSize = rowHeight * 2 / 3
    Row(
        modifier = modifier
            .height(rowHeight)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .offset(x = (imageSize.value * 0.6).dp)
                .paint(
                    painterResource(id = R.drawable.bubble),
                    contentScale = ContentScale.FillBounds
                )
                .align(Alignment.Top)
        ) {
            Text(
                text,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
            )

        }
            Image(
                painter = painterResource(res),
                contentDescription = null,
                modifier = Modifier
                    .height(imageSize)
                    .wrapContentWidth()
                    .align(Alignment.Bottom),
                contentScale = ContentScale.Fit
            )
    }


}

@Preview
@Composable
fun SpeechBubblePreview(modifier: Modifier = Modifier) {
    SpeechBubble(modifier
        .padding(top = 16.dp), 150.dp, "Psst!\nWanna hear a Cringe Joke?\n" +
            "Click here!",
        res = R.drawable.kotl) {

    }
}

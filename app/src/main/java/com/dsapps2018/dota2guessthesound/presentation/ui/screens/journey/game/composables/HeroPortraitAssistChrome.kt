package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.journey.HeroPortraitSlot
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

private val BadgeSize = 22.dp

@Composable
fun HeroPortraitWithAssists(
    slot: HeroPortraitSlot,
    imageSize: Dp,
    showCount: Boolean,
    soundCount: Int?,
    showPrimerBadge: Boolean,
    onPortraitClick: () -> Unit,
    onPrimerBadgeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageModifier = if (slot.blurRadiusDp > 0f) {
        Modifier
            .size(imageSize)
            .blur(radius = slot.blurRadiusDp.dp)
    } else {
        Modifier.size(imageSize)
    }

    Box(
        modifier = modifier
            .size(imageSize)
            .clickable(onClick = onPortraitClick),
    ) {
        Image(
            painter = painterResource(slot.imageResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = imageModifier,
        )
        if (showCount && soundCount != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .size(BadgeSize)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = soundCount.toString(),
                    color = JourneyTimeAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (showPrimerBadge) {
            Image(
                painter = painterResource(R.drawable.ic_story_music_on),
                contentDescription = stringResource(R.string.assist_primer_badge_cd),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(BadgeSize)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(3.dp)
                    .clickable(onClick = onPrimerBadgeClick),
            )
        }
    }
}

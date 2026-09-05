package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Quas

private val SoundCardShape = RoundedCornerShape(8.dp)

@Composable
fun SoundCard(
    selectedState: Boolean,
    onCardClicked: () -> Unit,
    onSoundIconClicked: () -> Unit,
    remainingPlays: Int? = null,
    modifier: Modifier = Modifier,
) {
    val haloAlpha by animateFloatAsState(
        targetValue = if (selectedState) 0.5f else 0f,
        label = "soundCardHalo",
    )
    val rimWidth by animateDpAsState(
        targetValue = if (selectedState) 2.dp else 0.dp,
        label = "soundCardRim",
    )

    Card(
        modifier = modifier
            .aspectRatio(2f / 2.75f)
            .clickable { onCardClicked() }
            .then(
                if (haloAlpha > 0.01f || rimWidth > 0.dp) {
                    Modifier.soundCardMark(
                        haloAlpha = haloAlpha,
                        rimWidth = rimWidth,
                        accent = Quas,
                    )
                } else {
                    Modifier
                }
            ),
        shape = SoundCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painterResource(
                    if (selectedState) R.drawable.card_bg_game_selected else R.drawable.card_bg_game
                ),
                modifier = Modifier.matchParentSize(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = this@BoxWithConstraints.maxHeight / 4)
                    .size(this@BoxWithConstraints.maxWidth / 2)
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .clickable {
                        // Only allow clicking if there are plays remaining (for Echo Limit affix)
                        if (remainingPlays == null || remainingPlays > 0) {
                            onSoundIconClicked()
                        }
                    },
                painter = painterResource(
                    if (remainingPlays != null && remainingPlays <= 0) {
                        R.drawable.card_sound_disabled // Show disabled if no plays left
                    } else if (selectedState) {
                        R.drawable.card_sound_enabled
                    } else {
                        R.drawable.card_sound_disabled
                    }
                ),
                contentDescription = null
            )

            // Echo Limit remaining plays — bottom center avoids selected-card corner checkmarks
            remainingPlays?.let { remaining ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = remaining.toString(),
                        color = if (remaining <= 2) Color.Red else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun Modifier.soundCardMark(
    haloAlpha: Float,
    rimWidth: Dp,
    accent: Color,
): Modifier = this
    .drawBehind {
        if (haloAlpha <= 0.01f) return@drawBehind
        val halo = 2.5.dp.toPx()
        drawRoundRect(
            color = accent.copy(alpha = haloAlpha),
            topLeft = Offset(-halo, -halo),
            size = Size(size.width + halo * 2, size.height + halo * 2),
            cornerRadius = CornerRadius(10.dp.toPx()),
        )
    }
    .then(
        if (rimWidth > 0.dp) {
            Modifier.border(
                width = rimWidth,
                color = accent.copy(alpha = 0.95f),
                shape = SoundCardShape,
            )
        } else {
            Modifier
        }
    )

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
private fun SoundCardPreview() {
    SoundCard(
        selectedState = false,
        onCardClicked = {},
        onSoundIconClicked = {},
        remainingPlays = 5,
        modifier = Modifier.width(96.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
private fun SoundCardSelectedPreview() {
    SoundCard(
        selectedState = true,
        onCardClicked = {},
        onSoundIconClicked = {},
        remainingPlays = 5,
        modifier = Modifier.width(96.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
private fun SoundCardMixedPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoundCard(
            selectedState = false,
            onCardClicked = {},
            onSoundIconClicked = {},
            modifier = Modifier.width(88.dp),
        )
        SoundCard(
            selectedState = true,
            onCardClicked = {},
            onSoundIconClicked = {},
            modifier = Modifier.width(88.dp),
        )
        SoundCard(
            selectedState = false,
            onCardClicked = {},
            onSoundIconClicked = {},
            modifier = Modifier.width(88.dp),
        )
        SoundCard(
            selectedState = true,
            onCardClicked = {},
            onSoundIconClicked = {},
            modifier = Modifier.width(88.dp),
        )
    }
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R

@Composable
fun SoundCard(
    selectedState: Boolean,
    onCardClicked: () -> Unit,
    onSoundIconClicked: () -> Unit,
    remainingPlays: Int? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .aspectRatio(2f / 2.75f)
            .clickable { onCardClicked() }
            .then(
                if (selectedState) {
                    Modifier.shadow(
                        elevation = 30.dp,
                        ambientColor = Color.Blue,
                        spotColor = Color.Cyan,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier.shadow(
                        elevation = 10.dp,
                        ambientColor = Color.Black,
                        spotColor = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            ),

        ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painterResource(if (selectedState) R.drawable.card_bg_game_selected else R.drawable.card_bg_game),
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

@Preview
@Composable
fun SoundCardPreview(modifier: Modifier = Modifier) {
    SoundCard(false, onCardClicked = {}, onSoundIconClicked = {}, remainingPlays = 5)
}
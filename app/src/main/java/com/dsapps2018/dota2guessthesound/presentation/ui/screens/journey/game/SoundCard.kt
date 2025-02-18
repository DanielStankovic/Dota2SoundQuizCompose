package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.data.model.JourneySoundModel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground

@Composable
fun SoundCard(
    modifier: Modifier = Modifier,
    startingState: Boolean = true
) {
    val selected = remember { mutableStateOf(startingState) }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = DialogBackground
        ),
        border = if (selected.value) BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.7f)) else null,
        modifier = Modifier
            .padding(8.dp)
            .clickable { selected.value = !selected.value }
            .then(
                if (selected.value) {
                    Modifier
                        .shadow(
                            elevation = 30.dp, ambientColor = Color.Blue, spotColor = Color.Cyan,
                            shape = RoundedCornerShape(8.dp)
                        )
                } else {
                    Modifier.shadow(
                        elevation = 30.dp, ambientColor = Color.Blue, spotColor = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            ),

        ) {
        Text(
            text = if (selected.value) "Selected" else "Not Selected",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
fun SoundCardPreview(modifier: Modifier = Modifier) {
    SoundCard()
}
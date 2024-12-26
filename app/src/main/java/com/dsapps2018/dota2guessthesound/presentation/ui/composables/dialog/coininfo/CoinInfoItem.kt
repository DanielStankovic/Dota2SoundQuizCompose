package com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.coininfo

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground

@Composable
fun CoinInfoItem(
    quizMode: String,
    value: String,
    @DrawableRes imageId: Int
) {
    Column {
        HorizontalDivider(color = DialogOnBackground)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quizMode,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$value = ",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.White
            )
            Image(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(30.dp),
                painter = painterResource(imageId),
                contentDescription = null
            )

        }
    }

}
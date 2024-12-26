package com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.coininfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinInfoDialog(
    onDismiss: () -> Unit
) {

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DialogBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.coin_info_title),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                IconButton(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 6.dp),
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))
            CoinInfoItem(
                quizMode = stringResource(R.string.quiz_lbl),
                value = stringResource(R.string.score_lbl),
                R.drawable.coin_unknown
            )

            CoinInfoItem(
                quizMode = stringResource(R.string.ff_30_lbl),
                value = "30",
                R.drawable.coin_30
            )

            CoinInfoItem(
                quizMode = stringResource(R.string.ff_60_lbl),
                value = "60",
                R.drawable.coin_60
            )

            CoinInfoItem(
                quizMode = stringResource(R.string.ff_90_lbl),
                value = "90",
                R.drawable.coin_90
            )
        }
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.toDp
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchAdContinueDialog(
    onDismiss: () -> Unit,
    onSkipClicked: () -> Unit,
    onWatchAdClicked: () -> Unit
) {

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DialogBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.game_over_lbl),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                stringResource(R.string.watch_add_to_continue_msg),
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(20.dp))
            HorizontalDivider(thickness = 1.dp, color = DialogOnBackground)
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.skip_btn),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.toDp())
                        .width(300.toDp())
                        .clickable {
                            onSkipClicked()
                        },
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.watch_add_continue),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.toDp())
                        .width(300.toDp())
                        .clickable {
                            onWatchAdClicked()
                            onDismiss()
                        },
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}
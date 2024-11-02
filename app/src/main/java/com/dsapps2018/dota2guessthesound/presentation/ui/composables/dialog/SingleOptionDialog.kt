package com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleOptionDialog(
    onDismiss: () -> Unit,
    onOptionClick: () -> Unit,
    titleText: String,
    messageText: String,
    optionText: String,
    dismissible: Boolean = true
) {

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible
        )
        ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(DialogBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    titleText,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Text(
                messageText,
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Spacer(modifier = Modifier.size(12.dp))
            HorizontalDivider(color = DialogOnBackground)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOptionClick()
                        onDismiss()

                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = optionText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    color = DialogOnBackground
                )
            }
        }
    }
}
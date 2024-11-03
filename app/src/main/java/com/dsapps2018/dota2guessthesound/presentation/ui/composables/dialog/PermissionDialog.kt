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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToSettings: () -> Unit,
    showRationale: Boolean
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(DialogBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.permission_required_lbl),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Text(
                if(!showRationale)
                    stringResource(R.string.permission_msg)
                else
                    stringResource(R.string.permission_msg_rationale),
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
                        if (!showRationale) {
                            onOkClick()
                            onDismiss()
                        } else {
                            onGoToSettings()
                            onDismiss()
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =  if(!showRationale) stringResource(R.string.grant_permission_lbl) else stringResource(
                        R.string.go_to_settings_lbl
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    color = DialogOnBackground
                )
            }
        }
    }
}
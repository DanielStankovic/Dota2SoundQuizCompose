package com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ZoomableImage
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground

@Composable
fun FullScreenImageDialog(onDismiss: () -> Unit, imageUrl: String) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp.value

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeight * 0.6).dp)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DialogBackground),
            contentAlignment = Alignment.Center
        ) {
            ZoomableImage(imageUrl)
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .padding(end = 8.dp),
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
    }
}
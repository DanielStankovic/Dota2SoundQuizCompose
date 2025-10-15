package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level.composables

import android.view.Menu
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@Composable
fun AffixInfoBottomSheet(
    affixName: String,
    affixDescription: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Let us control full width
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() } // tap outside to dismiss
                .background(Color.Black.copy(alpha = 0.5f)) // dim background
        ) {
            // Sliding animation
            val offsetY = remember { Animatable(300f) }

            LaunchedEffect(Unit) {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY.value.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
//                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.affix_board_bg), // your custom texture
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, top = 50.dp, bottom = 32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = affixName,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFA726)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = affixDescription,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
//                    MenuButton(
//                        modifier = Modifier
//                            .height(70.dp)
//                            .width(120.dp),
//                        paddingValues = PaddingValues(),
//                        text = "Close", textColor = Color.White,
//                        contentScale = ContentScale.Fit,
//                        onClick = onDismiss
//                    )
                }
            }
        }
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ReturnToCurrentLevelButton(
    pagerState: PagerState,
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isVisible by remember {
        derivedStateOf { pagerState.currentPage != currentLevel }
    }

    // Magical pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .wrapContentHeight(align = Alignment.Bottom)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Magical aura behind the button
            Box(
                modifier = Modifier
                    .size(width = 260.dp, height = 70.dp)
                    .graphicsLayer {
                        scaleX = 1.1f
                        scaleY = 1.1f
                        alpha = glowAlpha
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFD700).copy(alpha = 0.4f), Color.Transparent),
                            center = Offset.Zero,
                            radius = 400f
                        ),
                        shape = RoundedCornerShape(50)
                    )
            )

            // The main button
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(currentLevel)
                        // You can play a short sound here like “whoosh” or “blink”
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3C2F2F),
                            Color(0xFF2B1F1F)
                        )
                    ).toSolidColor(),
                    contentColor = Color(0xFFFFD700)
                ),
                border = BorderStroke(2.dp, Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFFAA00)
                    )
                )),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .width(240.dp)
                    .height(55.dp)
                    .shadow(10.dp, shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Return to Hero’s Path",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

// Extension for using gradient as solid color
fun Brush.toSolidColor(): Color =
    if (this is SolidColor) this.value else Color.Unspecified
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import kotlin.random.Random

@Composable
fun JourneyLevelResultScreen(
    modifier: Modifier = Modifier,
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    level: Int,
    isLevelCompleted: Boolean,
    onPlayClicked: (Int) -> Unit
) {
    val backgroundColor = if (isLevelCompleted) Color(0xFF101820) else Color(0xFF1A0000)
    val titleColor = if (isLevelCompleted) Color(0xFFFFD700) else Color(0xFFFF4C4C)
    val buttonColor = if (isLevelCompleted) Color(0xFF4CAF50) else Color(0xFFD32F2F)
    val titleText = if (isLevelCompleted) "Level $level Completed!" else "Level $level Failed!"
    val buttonText = if (isLevelCompleted) "Continue Journey" else "Try Again"

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    if (isLevelCompleted) {
        scoreViewModel.updateJourneyLevel(level)
    }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.journey_game_bg2),
                        contentScale = ContentScale.Crop
                    ),

                ) {

                // Confetti or falling sparkles depending on result
                if (isLevelCompleted) {
                    ConfettiEffect(true) // colorful confetti
                } else {
                    AshParticlesEffect(false) // subtle darker “failure” effect
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = titleText,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .scale(scale)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MenuButton(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(200.dp),
                        paddingValues = PaddingValues(),
                        text = buttonText, textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        onPlayClicked(if (isLevelCompleted) level + 1 else level)
                    }
//                    Button(
//                        onClick = onContinueClicked,
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF4CAF50)
//                        ),
//                        modifier = Modifier
//                            .padding(horizontal = 24.dp)
//                            .fillMaxWidth(0.6f)
//                    ) {
//                        Text(
//                            text = "Continue Journey",
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
                }
            }
        }
    )
}

@Composable
fun ConfettiEffect(isLevelCompleted: Boolean) {
    val confettiCount = 50
    val confetti = remember { List(confettiCount) { ConfettiPiece.random(isLevelCompleted) } }
    val duration = 4000

    // This drives recomposition every frame
    var time by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                time = frameTime
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val elapsed = (time / 1_000_000L) % duration
        val progress = elapsed.toFloat() / duration

        confetti.forEach { piece ->
            val y = (size.height * progress * piece.speed) % size.height
            drawCircle(
                color = piece.color,
                radius = piece.size,
                center = Offset(piece.x * size.width, y)
            )
        }
    }
}

private data class ConfettiPiece(
    var x: Float,
    var size: Float,
    var speed: Float,
    var color: Color
) {
    companion object {
        private val successColors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFF69B4),
            Color(0xFF00FFFF),
            Color(0xFFADFF2F),
            Color(0xFF87CEFA)
        )
        private val failColors = listOf(
            Color(0xFF888888),
            Color(0xFF444444),
            Color(0xFF999999),
            Color(0xFF555555)
        )

        fun random(isSuccess: Boolean): ConfettiPiece {
            val colors = if (isSuccess) successColors else failColors
            return ConfettiPiece(
                x = Random.nextFloat(),
                size = Random.nextFloat() * 8 + 3,
                speed = Random.nextFloat() * 1.5f + 0.5f,
                color = colors.random()
            )
        }
    }
}

@Composable
fun AshParticlesEffect(isLevelCompleted: Boolean) {
    val particles = remember { List(40) { ConfettiPiece.random(isLevelCompleted) } }

    // State to drive animation
    var time by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                time = frameTime
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val progress = (time / 1_000_000L / 4000f) % 1f // normalized 0..1 over 4s

        particles.forEach { piece ->
            val y = (size.height * progress * piece.speed) % size.height
            val x = (piece.x * size.width + progress * 20) % size.width // drift sideways
            drawCircle(
                color = piece.color.copy(alpha = 0.3f),
                radius = piece.size,
                center = Offset(x, y)
            )
        }
    }
}
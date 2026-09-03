package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch

/**
 * One-shot Board Quake visual/haptic pulse. Idle when [nonce] is null — no running
 * animators on non-Soundquake levels or between quakes.
 */
data class SoundquakeFxRequest(
    val nonce: Long,
    val strong: Boolean,
    val drainedHeart: Boolean,
)

class SoundquakeFxState internal constructor(
    val shakeX: Animatable<Float, AnimationVector1D>,
    val shakeY: Animatable<Float, AnimationVector1D>,
    val dustAlpha: Animatable<Float, AnimationVector1D>,
    val heartScale: Animatable<Float, AnimationVector1D>,
)

@Composable
fun rememberSoundquakeFxState(
    request: SoundquakeFxRequest?,
): SoundquakeFxState {
    val shakeX = remember { Animatable(0f) }
    val shakeY = remember { Animatable(0f) }
    val dustAlpha = remember { Animatable(0f) }
    val heartScale = remember { Animatable(1f) }
    val view = LocalView.current

    LaunchedEffect(request?.nonce) {
        val fx = request ?: return@LaunchedEffect
        val amplitude = if (fx.strong) 16f else 9f
        val pulses = if (fx.strong) 5 else 3

        view.performHapticFeedback(
            if (fx.strong) {
                HapticFeedbackConstants.LONG_PRESS
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            }
        )

        launch {
            dustAlpha.snapTo(if (fx.strong) 0.42f else 0.28f)
            dustAlpha.animateTo(0f, tween(durationMillis = if (fx.strong) 520 else 380))
        }

        launch {
            repeat(pulses) {
                shakeX.animateTo(amplitude, tween(38))
                shakeY.animateTo(amplitude * 0.45f, tween(38))
                shakeX.animateTo(-amplitude, tween(38))
                shakeY.animateTo(-amplitude * 0.45f, tween(38))
            }
            shakeX.animateTo(0f, tween(60))
            shakeY.animateTo(0f, tween(60))
        }

        if (fx.drainedHeart) {
            launch {
                heartScale.animateTo(1.4f, tween(110))
                heartScale.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                )
            }
        }
    }

    return remember {
        SoundquakeFxState(shakeX, shakeY, dustAlpha, heartScale)
    }
}

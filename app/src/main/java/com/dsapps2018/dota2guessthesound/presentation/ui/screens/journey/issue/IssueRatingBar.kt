package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import kotlinx.coroutines.delay

/**
 * Interactive 1–5 Issue Rating control. Tap commits immediately; same star as
 * current is a no-op. A short bounce confirms a successful change.
 */
@Composable
fun IssueRatingBar(
    stars: Int?,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmPulse by remember { mutableIntStateOf(0) }
    var lastCommitted by remember { mutableStateOf(stars) }

    LaunchedEffect(stars) {
        if (stars != null && stars != lastCommitted) {
            lastCommitted = stars
            confirmPulse += 1
        } else if (stars == lastCommitted) {
            // Keep lastCommitted in sync when loading an existing Rating.
            lastCommitted = stars
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                if (stars == null) R.string.issue_rating_prompt
                else R.string.issue_rating_thanks
            ),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (value in 1..5) {
                key(value) {
                    val filled = stars != null && value <= stars
                    val scale = remember { Animatable(1f) }
                    LaunchedEffect(confirmPulse, stars) {
                        if (confirmPulse == 0) return@LaunchedEffect
                        if (stars == null || value > stars) {
                            scale.snapTo(1f)
                            return@LaunchedEffect
                        }
                        scale.snapTo(1f)
                        delay((value - 1) * 35L)
                        scale.animateTo(
                            targetValue = 1.28f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                        )
                    }
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = stringResource(R.string.issue_rating_star_cd, value),
                        tint = if (filled) JourneyTimeAccent else Color.White.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer {
                                scaleX = scale.value
                                scaleY = scale.value
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onRate(value) },
                            )
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}

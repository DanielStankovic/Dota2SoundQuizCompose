package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.level.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.PlayLevel
import kotlin.math.roundToInt

/** Vertical space reserved above the banner for the path scrubber + gap. */
val JourneyPathScrubberReserve = 56.dp

private val ThumbSize = 18.dp
private val TrackHeight = 10.dp

/**
 * Horizontal journey path rail: drag or tap to jump across the level catalog.
 * Cleared stretch uses moonlight accent; frontier is a PlayLevel diamond; thumb follows the pager.
 */
@Composable
fun JourneyPathScrubber(
    pageCount: Int,
    visualPage: Float,
    frontierPage: Int,
    pageToLevel: (Int) -> Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) return

    var dragging by remember { mutableStateOf(false) }
    var dragPage by remember { mutableIntStateOf(0) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val maxPage = (pageCount - 1).toFloat()
    val settledFraction = (visualPage / maxPage).coerceIn(0f, 1f)
    val frontierFraction = (frontierPage / maxPage).coerceIn(0f, 1f)
    val thumbFraction = if (dragging) dragFraction else settledFraction
    val displayLevel = pageToLevel(
        if (dragging) dragPage else visualPage.roundToInt().coerceIn(0, pageCount - 1)
    )

    val bubbleAlpha by animateFloatAsState(
        targetValue = if (dragging) 1f else 0f,
        animationSpec = tween(120),
        label = "pathScrubberBubble",
    )

    val cd = stringResource(
        R.string.journey_path_scrubber_cd,
        displayLevel,
        pageToLevel(pageCount - 1),
    )

    val density = LocalDensity.current
    val thumbPx = with(density) { ThumbSize.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(JourneyPathScrubberReserve)
            .semantics { contentDescription = cd }
            .pointerInput(pageCount, thumbPx) {
                val width = size.width.toFloat().coerceAtLeast(1f)
                val inset = thumbPx / 2f
                val usable = (width - thumbPx).coerceAtLeast(1f)
                fun fractionAt(x: Float): Float =
                    ((x - inset) / usable).coerceIn(0f, 1f)
                fun pageAt(x: Float): Int =
                    (fractionAt(x) * maxPage).roundToInt().coerceIn(0, pageCount - 1)

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    dragging = true
                    dragFraction = fractionAt(down.position.x)
                    dragPage = pageAt(down.position.x)
                    onPageSelected(dragPage)
                    drag(down.id) { change ->
                        change.consume()
                        dragFraction = fractionAt(change.position.x)
                        val page = pageAt(change.position.x)
                        if (page != dragPage) {
                            dragPage = page
                            onPageSelected(page)
                        }
                    }
                    dragging = false
                }
            },
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val inset = thumbPx / 2f
        val usable = (widthPx - thumbPx).coerceAtLeast(1f)
        val thumbCenterX = inset + thumbFraction * usable

        if (bubbleAlpha > 0.01f) {
            val bubbleHalf = with(density) { 22.dp.toPx() }
            val bubbleX = (thumbCenterX - bubbleHalf)
                .coerceIn(0f, (widthPx - bubbleHalf * 2f).coerceAtLeast(0f))
                .roundToInt()
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset { IntOffset(bubbleX, 0) }
                    .widthIn(min = 44.dp),
                shape = RoundedCornerShape(10.dp),
                color = JourneyButtonBackground.copy(alpha = 0.94f * bubbleAlpha),
                border = BorderStroke(
                    1.dp,
                    JourneyTimeAccent.copy(alpha = 0.55f * bubbleAlpha),
                ),
            ) {
                Text(
                    text = displayLevel.toString(),
                    color = JourneyTimeAccent.copy(alpha = bubbleAlpha),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f,
                        )
                    ),
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .align(Alignment.BottomStart)
                .padding(bottom = 2.dp),
        ) {
            val trackY = size.height / 2f
            val trackH = size.height
            val corner = CornerRadius(trackH / 2f, trackH / 2f)
            val localInset = thumbPx / 2f
            val localUsable = (size.width - thumbPx).coerceAtLeast(1f)
            val fillEnd = localInset + frontierFraction * localUsable
            val fx = localInset + frontierFraction * localUsable

            drawRoundRect(
                color = JourneyButtonBackground.copy(alpha = 0.92f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, trackH),
                cornerRadius = corner,
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.12f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, trackH),
                cornerRadius = corner,
                style = Stroke(width = 1.dp.toPx()),
            )

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PlayLevel.copy(alpha = 0.55f),
                        JourneyTimeAccent.copy(alpha = 0.75f),
                    ),
                ),
                topLeft = Offset(0f, 0f),
                size = Size(fillEnd.coerceAtLeast(trackH), trackH),
                cornerRadius = corner,
            )

            val diamond = Path().apply {
                moveTo(fx, trackY - trackH * 0.85f)
                lineTo(fx + trackH * 0.7f, trackY)
                lineTo(fx, trackY + trackH * 0.85f)
                lineTo(fx - trackH * 0.7f, trackY)
                close()
            }
            drawPath(diamond, color = PlayLevel)
            drawPath(
                diamond,
                color = Color.White.copy(alpha = 0.55f),
                style = Stroke(width = 1.dp.toPx()),
            )
        }

        val trackPx = with(density) { TrackHeight.toPx() }
        // Thumb is taller than the rail; nudge it down so centers share the same line.
        val thumbCenterNudgeY = ((thumbPx - trackPx) / 2f).roundToInt()
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 2.dp)
                .offset {
                    IntOffset(
                        x = (thumbCenterX - thumbPx / 2f).roundToInt(),
                        y = thumbCenterNudgeY,
                    )
                }
                .size(ThumbSize)
                .border(1.5.dp, JourneyTimeAccent.copy(alpha = 0.9f), CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, JourneyTimeAccent.copy(alpha = 0.85f)),
                    ),
                    shape = CircleShape,
                ),
        )
    }
}

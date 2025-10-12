package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.data.model.LevelModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircularCarouselList(
    lazyListState: LazyListState,
    levels: List<LevelModel>,
    totalItems: Int,
    startIndex: Int,
    itemSize: Dp,
    visualItemSize: Dp,
    radiusPx: Float,
    density: Density,
    userCompletedLevel: Int = 5,
    onLevelClicked: (Int) -> Unit,
) {
    val currentLevel = userCompletedLevel + 1
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    // Create a custom fling behavior that reduces scroll velocity
    val customFlingBehavior = remember {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // Reduce the velocity by 30% to slow down scrolling
                val reducedVelocity = initialVelocity * 0.0f
                return with(snapFlingBehavior) {

                    performFling(reducedVelocity) }
            }
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        flingBehavior = customFlingBehavior
    ) {
        items(count = totalItems, key = { it }) { index ->
            val actualIndex = (index - startIndex).mod(levels.size)
            val level = levels[actualIndex]

            CarouselItem(
                level = level,
                lazyListState = lazyListState,
                itemSize = itemSize,
                visualItemSize = visualItemSize,
                radiusPx = radiusPx,
                density = density,
                isCompleted = level.level <= userCompletedLevel,
                isCurrentLevel = level.level == currentLevel,
                isLocked = level.level > currentLevel,
                onClick = { onLevelClicked(level.level) }
            )
        }
    }
}

@Composable
fun CarouselItem(
    level: LevelModel,
    lazyListState: LazyListState,
    itemSize: Dp,
    radiusPx: Float,
    density: Density,
    visualItemSize: Dp,
    isCompleted: Boolean,
    isCurrentLevel: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    var itemOffset by remember { mutableFloatStateOf(0f) }
    var itemWidth by remember { mutableFloatStateOf(0f) }

    val buttonColors = when {
        isCurrentLevel -> listOf(Color(0xFF4CAF50), Color(0xFF2196F3)) // Green to Blue gradient
        isCompleted -> listOf(Color(0xFF2196F3), Color(0xFF1976D2)) // Blue gradient
        else -> listOf(Color(0xFF424242), Color(0xFF616161)) // Gray gradient
    }

    val borderColor = when {
        isCurrentLevel -> Color(0xFFFFD700) // Gold border for current level
        isCompleted -> Color(0xFF2196F3)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(itemSize)
            .onPlaced { coordinates ->
                itemOffset = coordinates.positionInParent().x
                itemWidth = coordinates.size.width.toFloat()
            }
            .graphicsLayer {
                // Get viewport and item position
                val viewportWidth = lazyListState.layoutInfo.viewportSize.width.toFloat()
                val viewportCenter = viewportWidth / 2f
                val itemCenter = itemOffset + (itemWidth / 2f)
                val distanceFromCenter = itemCenter - viewportCenter
                // Calculate offset as a fraction of item width
                val pageOffsetFraction = distanceFromCenter / (itemWidth + density.density)
                // Map offset to angle for circular effect
                val angleRad = pageOffsetFraction * PI * 0.25f

                // Normalize the visual size
                val visualScale = visualItemSize / itemSize
                scaleX = visualScale
                scaleY = visualScale

                // X: horizontal position on circle (sin)
                translationX = (radiusPx * sin(angleRad)).toFloat()
                // Y: vertical position on circle (1-cos)
                translationY = (radiusPx * (1 - cos(angleRad * 2))).toFloat()

                // Fade out items near the edge
                // simply 1f - abs(pageOffsetFraction).coerceIn(0f,1f)
                // but sometimes fraction is not exactly 1f so just round it
                alpha = (1f - abs(pageOffsetFraction)
                    .coerceIn(0f, 0.75f)
                    .times(1.34f))
            }
            .clip(CircleShape)
            .border(
                width = if (isCurrentLevel) 3.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(
                brush = Brush.verticalGradient(buttonColors),
                shape = CircleShape
            )
            .clickable(enabled = !isLocked) {
                if (!isLocked) onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            isLocked -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
            isCompleted && !isCurrentLevel -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = level.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            isCurrentLevel -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Current Level",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = level.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else -> {
                Text(
                    text = level.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
//        AsyncImage(
//            model = developer.profileImage,
//            modifier = Modifier
//                .fillMaxSize()
//                .graphicsLayer {
//                    // Ignore this part, just for better visual. Use headshot images
//                    scaleX = 1.75f
//                    scaleY = 1.75f
//                    translationY = itemSize.toPx() * 0.25f
//                },
//            contentScale = ContentScale.Crop,
//            contentDescription = developer.name
//        )
    }
}
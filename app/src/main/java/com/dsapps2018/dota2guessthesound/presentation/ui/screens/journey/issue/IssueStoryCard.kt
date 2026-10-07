package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import java.io.File

/**
 * Shared cover row for Issue Picker and Issue Library.
 * Synopsis collapses to 3 lines; expand/collapse is separate from [onClick].
 */
@Composable
fun IssueStoryCard(
    title: String,
    synopsis: String,
    coverFilePath: String,
    metaLine: String,
    onClick: () -> Unit,
    expanded: Boolean,
    onToggleSynopsis: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    enabled: Boolean = true,
    locked: Boolean = false,
) {
    val shape = RoundedCornerShape(16.dp)
    val canOpen = enabled && !locked
    Column(
        modifier
            .fillMaxWidth()
            .alpha(if (locked) 0.62f else if (enabled) 1f else 0.55f)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.04f),
                    )
                )
            )
            .border(
                1.dp,
                JourneyTimeAccent.copy(alpha = when {
                    locked -> 0.22f
                    enabled -> 0.4f
                    else -> 0.18f
                }),
                shape,
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(enabled = canOpen, onClick = onClick)
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box {
                val coverModel = coverFilePath.takeIf { it.isNotBlank() }?.let { File(it) }
                AsyncImage(
                    model = coverModel,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(92.dp)
                        .aspectRatio(0.72f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C2A36)),
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f))
                            )
                        ),
                )
                if (locked) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_story_lock),
                            contentDescription = stringResource(R.string.issue_library_cd_locked),
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                if (badge != null) {
                    Text(
                        text = badge,
                        color = JourneyTimeAccent.copy(alpha = 0.95f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = metaLine,
                    color = JourneyTimeAccent.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (synopsis.isNotBlank() && !expanded) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = synopsis,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                }
            }
        }

        if (synopsis.isNotBlank()) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Text(
                    text = synopsis,
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                )
            }
            Text(
                text = stringResource(
                    if (expanded) R.string.issue_synopsis_less else R.string.issue_synopsis_more
                ),
                color = JourneyTimeAccent.copy(alpha = 0.95f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp, top = 2.dp)
                    .clickable(onClick = onToggleSynopsis),
            )
        }
    }
}

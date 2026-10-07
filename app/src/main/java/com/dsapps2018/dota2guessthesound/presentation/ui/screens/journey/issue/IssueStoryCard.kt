package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import java.io.File

/**
 * Shared cover row for Issue Picker and Issue Library.
 */
@Composable
fun IssueStoryCard(
    title: String,
    synopsis: String,
    coverFilePath: String,
    metaLine: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.55f)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.04f),
                    )
                )
            )
            .border(1.dp, JourneyTimeAccent.copy(alpha = if (enabled) 0.4f else 0.18f), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
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
            if (synopsis.isNotBlank()) {
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
}

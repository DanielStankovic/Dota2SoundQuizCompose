package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.data.progress.JourneyIssue
import com.dsapps2018.dota2guessthesound.data.progress.ReadingLocales
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

data class ReadingLocaleOption(
    val code: String,
    val label: String,
)

@Composable
fun ReadingLocalePicker(
    locales: List<ReadingLocaleOption>,
    selectedCode: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(locales, key = { it.code }) { option ->
            ReadingLocaleRow(
                label = option.label,
                selected = option.code == selectedCode,
                onClick = { onSelect(option.code) },
            )
        }
    }
}

@Composable
private fun ReadingLocaleRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) JourneyTimeAccent.copy(alpha = 0.16f)
                else Color.White.copy(alpha = 0.06f)
            )
            .border(
                width = 1.dp,
                color = if (selected) JourneyTimeAccent.copy(alpha = 0.85f)
                else Color.White.copy(alpha = 0.12f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) JourneyTimeAccent else Color.White.copy(alpha = 0.35f),
                    shape = CircleShape,
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(JourneyTimeAccent)
                )
            }
        }
    }
}

fun curatedReadingLocaleOptions(): List<ReadingLocaleOption> =
    ReadingLocales.all.map { code ->
        ReadingLocaleOption(code = code, label = ReadingLocales.displayName(code))
    }

@Preview(showBackground = true, backgroundColor = 0xFF0B1218)
@Composable
private fun ReadingLocalePickerPreview() {
    Column(Modifier.padding(16.dp)) {
        ReadingLocalePicker(
            locales = curatedReadingLocaleOptions().take(6),
            selectedCode = JourneyIssue.ENGLISH,
            onSelect = {},
        )
        Spacer(Modifier.height(8.dp))
    }
}

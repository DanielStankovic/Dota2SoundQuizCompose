package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.AffixModel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

@Composable
fun AffixTrayHandle(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Image(
        painter = painterResource(R.drawable.affix_tray_tab),
        contentDescription = stringResource(R.string.affix_tray_cd),
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(width = 40.dp, height = 56.dp)
            .minimumInteractiveComponentSize()
            .graphicsLayer { scaleX = if (expanded) -1f else 1f }
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    )
}

@Composable
fun AffixTrayPanel(
    affixes: List<AffixModel>,
    awaitingBegin: Boolean,
    onAffixClick: (AffixModel) -> Unit,
    onBegin: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    enabled = !awaitingBegin,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss,
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(0.82f)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AffixTrayHandle(
                expanded = true,
                enabled = !awaitingBegin,
                onClick = onDismiss,
            )
            Box(
                modifier = Modifier
                    .width(252.dp)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(R.drawable.affix_tray_panel),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize(),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 40.dp, end = 36.dp, top = 36.dp, bottom = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.affix_tray_title),
                        color = JourneyTimeAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        affixes.forEach { affix ->
                            AffixTrayRow(
                                affix = affix,
                                onClick = { onAffixClick(affix) },
                            )
                        }
                    }
                    if (awaitingBegin) {
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(168.dp)
                                .height(52.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    ambientColor = JourneyTimeAccent.copy(alpha = 0.35f),
                                    spotColor = JourneyTimeAccent.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .paint(
                                    painterResource(R.drawable.affix_tray_begin),
                                    contentScale = ContentScale.Fit,
                                )
                                .clickable(onClick = onBegin),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.journey_round_begin),
                                color = JourneyTimeAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AffixTrayRow(
    affix: AffixModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Image(
            painter = painterResource(affix.iconResourceId),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Text(
            text = affix.affix,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

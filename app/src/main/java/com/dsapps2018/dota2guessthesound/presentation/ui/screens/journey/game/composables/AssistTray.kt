package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
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
import com.dsapps2018.dota2guessthesound.data.journey.AssistDisableReason
import com.dsapps2018.dota2guessthesound.data.journey.JourneyAssistUiState
import com.dsapps2018.dota2guessthesound.data.progress.msUntilNextUtcMidnight
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.CoinAdGrantCard
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.formatDailyReset
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import kotlinx.coroutines.delay

@Composable
fun AssistTrayHandle(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Image(
        painter = painterResource(R.drawable.affix_tray_tab),
        contentDescription = stringResource(R.string.assist_tray_cd),
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(width = 40.dp, height = 56.dp)
            .minimumInteractiveComponentSize()
            // Mirror Affix handle: face inward toward the left panel.
            .graphicsLayer { scaleX = if (expanded) 1f else -1f }
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    )
}

@Composable
fun AssistTrayPanel(
    assist: JourneyAssistUiState,
    highlightSoundSampler: Boolean,
    onDismiss: () -> Unit,
    onBuyCount: () -> Unit,
    onUnlockSoundSamplerAd: () -> Unit,
    onWatchAdForCoins: () -> Unit,
    onRefreshGrantStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val soundSamplerAtDailyCap =
        !assist.soundSamplerUnlocked &&
            assist.soundSamplerDisabledReason == null &&
            !assist.soundSamplerAdUnlock.canUnlock
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(soundSamplerAtDailyCap) {
        if (!soundSamplerAtDailyCap) return@LaunchedEffect
        while (true) {
            delay(1_000L)
            nowMs = System.currentTimeMillis()
            onRefreshGrantStatus()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss,
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight(0.82f)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(268.dp)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(R.drawable.affix_tray_panel),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { scaleX = -1f },
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 36.dp, end = 40.dp, top = 36.dp, bottom = 44.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.assist_tray_title),
                        color = JourneyTimeAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.assist_tray_coins,
                            assist.coinBalance,
                        ),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(12.dp))

                    AssistOfferCard(
                        title = stringResource(R.string.assist_count_title),
                        body = stringResource(R.string.assist_count_body),
                        unlocked = assist.countUnlocked,
                        disabledReason = assist.countDisabledReason,
                        highlight = false,
                        primaryLabel = when {
                            assist.countUnlocked -> stringResource(R.string.assist_unlocked)
                            assist.countDisabledReason != null -> null
                            assist.canAffordCount -> stringResource(
                                R.string.assist_buy_coins,
                                assist.countCost,
                            )
                            else -> stringResource(
                                R.string.assist_need_coins,
                                assist.countCost,
                            )
                        },
                        primaryEnabled = assist.countPurchaseAvailable && assist.canAffordCount,
                        onPrimary = onBuyCount,
                    )

                    Spacer(Modifier.height(10.dp))

                    val soundSamplerUsedToday =
                        (Constants.SOUND_SAMPLER_AD_UNLOCK_DAILY_CAP - assist.soundSamplerAdUnlock.remainingToday)
                            .coerceIn(0, Constants.SOUND_SAMPLER_AD_UNLOCK_DAILY_CAP)
                    val soundSamplerFooter = if (
                        !assist.soundSamplerUnlocked && assist.soundSamplerDisabledReason == null
                    ) {
                        buildString {
                            append(
                                stringResource(
                                    R.string.assist_sound_sampler_daily_progress,
                                    soundSamplerUsedToday,
                                    Constants.SOUND_SAMPLER_AD_UNLOCK_DAILY_CAP,
                                )
                            )
                            if (soundSamplerAtDailyCap) {
                                append('\n')
                                append(
                                    stringResource(
                                        R.string.assist_daily_limit_resets,
                                        formatDailyReset(msUntilNextUtcMidnight(nowMs)),
                                    )
                                )
                            }
                        }
                    } else {
                        null
                    }
                    AssistOfferCard(
                        title = stringResource(R.string.assist_sound_sampler_title),
                        body = stringResource(R.string.assist_sound_sampler_body),
                        unlocked = assist.soundSamplerUnlocked,
                        disabledReason = assist.soundSamplerDisabledReason,
                        highlight = highlightSoundSampler,
                        primaryLabel = when {
                            assist.soundSamplerUnlocked -> stringResource(R.string.assist_unlocked)
                            assist.soundSamplerDisabledReason != null -> null
                            !assist.soundSamplerAdUnlock.canUnlock ->
                                stringResource(R.string.assist_coin_ad_daily_cap)
                            else -> stringResource(R.string.assist_watch_ad_unlock)
                        },
                        primaryEnabled = assist.soundSamplerPurchaseAvailable,
                        onPrimary = onUnlockSoundSamplerAd,
                        footer = soundSamplerFooter,
                    )

                    if (assist.coinBalance < assist.countCost) {
                        Spacer(Modifier.height(12.dp))
                        CoinAdGrantCard(
                            grant = assist.coinAdGrant,
                            onWatchAdForCoins = onWatchAdForCoins,
                            onRefreshGrantStatus = onRefreshGrantStatus,
                        )
                    }
                }
            }
            AssistTrayHandle(
                expanded = true,
                onClick = onDismiss,
            )
        }
    }
}

@Composable
private fun AssistOfferCard(
    title: String,
    body: String,
    unlocked: Boolean,
    disabledReason: AssistDisableReason?,
    highlight: Boolean,
    primaryLabel: String?,
    primaryEnabled: Boolean,
    onPrimary: () -> Unit,
    footer: String? = null,
) {
    val locked = disabledReason != null
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    highlight -> Color(0xFF2A4A3A)
                    locked -> Color(0xFF2A2A2A)
                    unlocked -> Color(0xFF1E3A2A)
                    else -> Color(0xFF1A2430)
                }
            )
            .padding(10.dp)
            .alpha(if (locked) 0.72f else 1f),
    ) {
        Text(
            text = title,
            color = JourneyTimeAccent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
        )
        if (disabledReason != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(disabledReason.toStringRes()),
                color = Color(0xFFFFB4A8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        if (primaryLabel != null) {
            Spacer(Modifier.height(8.dp))
            AssistActionButton(
                label = primaryLabel,
                enabled = primaryEnabled && !unlocked,
                onClick = onPrimary,
            )
        }
        if (footer != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = footer,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun AssistActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .paint(
                painterResource(R.drawable.affix_tray_begin),
                contentScale = ContentScale.FillBounds,
            )
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = JourneyTimeAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

private fun AssistDisableReason.toStringRes(): Int = when (this) {
    AssistDisableReason.AmongHeroes -> R.string.assist_disabled_among_heroes
    AssistDisableReason.UnknownCount -> R.string.assist_disabled_unknown_count
}

package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrantStatus
import com.dsapps2018.dota2guessthesound.data.progress.msUntilNextUtcMidnight
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import kotlinx.coroutines.delay

enum class CoinAdGrantCardStyle {
    /** Assist tray: fills tray column, dark panel behind content. */
    AssistPanel,

    /** Home Invoker: compact, no blue panel (home art already fills the screen). */
    HomeOverlay,
}

/**
 * Shared Coin Ad Grant chrome for Assist tray and Home Invoker.
 * Daily progress uses UTC day (see [com.dsapps2018.dota2guessthesound.data.progress.CoinAdGrant]).
 */
@Composable
fun CoinAdGrantCard(
    grant: CoinAdGrantStatus,
    onWatchAdForCoins: () -> Unit,
    onRefreshGrantStatus: () -> Unit,
    modifier: Modifier = Modifier,
    style: CoinAdGrantCardStyle = CoinAdGrantCardStyle.AssistPanel,
) {
    val atDailyCap = grant.remainingToday <= 0
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(grant.cooldownRemainingMs, atDailyCap) {
        if (grant.cooldownRemainingMs <= 0L && !atDailyCap) return@LaunchedEffect
        while (true) {
            delay(1_000L)
            nowMs = System.currentTimeMillis()
            onRefreshGrantStatus()
        }
    }

    val usedToday =
        (Constants.COIN_AD_GRANT_DAILY_CAP - grant.remainingToday).coerceIn(
            0,
            Constants.COIN_AD_GRANT_DAILY_CAP,
        )
    val resetInMs = msUntilNextUtcMidnight(nowMs)

    val columnModifier = when (style) {
        CoinAdGrantCardStyle.AssistPanel -> modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1A2430))
            .padding(10.dp)
        CoinAdGrantCardStyle.HomeOverlay -> modifier
            .wrapContentWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    }

    val bodyShadow = if (style == CoinAdGrantCardStyle.HomeOverlay) {
        TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.85f),
                blurRadius = 6f,
            ),
        )
    } else {
        TextStyle.Default
    }

    val columnAlignment = when (style) {
        CoinAdGrantCardStyle.AssistPanel -> Alignment.Start
        CoinAdGrantCardStyle.HomeOverlay -> Alignment.CenterHorizontally
    }
    val bodyAlign = when (style) {
        CoinAdGrantCardStyle.AssistPanel -> TextAlign.Start
        CoinAdGrantCardStyle.HomeOverlay -> TextAlign.Center
    }
    val footerAlign = when (style) {
        CoinAdGrantCardStyle.AssistPanel -> TextAlign.Start
        CoinAdGrantCardStyle.HomeOverlay -> TextAlign.Center
    }

    Column(
        modifier = columnModifier,
        horizontalAlignment = columnAlignment,
    ) {
        if (style == CoinAdGrantCardStyle.AssistPanel) {
            Text(
                text = stringResource(R.string.assist_coin_ad_title),
                color = JourneyTimeAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
        }
        Text(
            text = stringResource(
                R.string.assist_coin_ad_body,
                grant.grantAmount,
            ),
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            textAlign = bodyAlign,
            style = bodyShadow,
        )
        Spacer(Modifier.height(8.dp))

        val label = when {
            grant.remainingToday <= 0 -> stringResource(R.string.assist_coin_ad_daily_cap)
            grant.cooldownRemainingMs > 0L -> stringResource(
                R.string.assist_coin_ad_cooldown,
                formatAssistCooldown(grant.cooldownRemainingMs),
            )
            else -> stringResource(R.string.assist_watch_ad_coins, grant.grantAmount)
        }
        val buttonModifier = when (style) {
            CoinAdGrantCardStyle.AssistPanel -> Modifier.fillMaxWidth()
            CoinAdGrantCardStyle.HomeOverlay -> Modifier.width(200.dp)
        }
        Box(
            modifier = buttonModifier
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .paint(
                    painterResource(R.drawable.affix_tray_begin),
                    contentScale = ContentScale.FillBounds,
                )
                .alpha(if (grant.canGrant) 1f else 0.45f)
                .clickable(enabled = grant.canGrant, onClick = onWatchAdForCoins),
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
        Spacer(Modifier.height(6.dp))
        val footerShadow = TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.85f),
                blurRadius = 6f,
            ),
        )
        Text(
            text = stringResource(
                R.string.assist_coin_ad_daily_progress,
                usedToday,
                Constants.COIN_AD_GRANT_DAILY_CAP,
            ),
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = footerAlign,
            style = footerShadow,
        )
        if (atDailyCap) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(
                    R.string.assist_daily_limit_resets,
                    formatDailyReset(resetInMs),
                ),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = footerAlign,
                style = footerShadow,
            )
        }
    }
}

/** Short cooldown: `m:ss` while over a minute, then whole seconds only. */
fun formatAssistCooldown(remainingMs: Long): String {
    val totalSec = ((remainingMs + 999L) / 1000L).coerceAtLeast(0L).toInt()
    return if (totalSec > 60) {
        "%d:%02d".format(totalSec / 60, totalSec % 60)
    } else {
        "${totalSec}s"
    }
}

/** Daily-cap reset: `h:mm:ss`, or `m:ss` under an hour. */
fun formatDailyReset(remainingMs: Long): String {
    val totalSec = ((remainingMs + 999L) / 1000L).coerceAtLeast(0L).toInt()
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.progress.ChapterReaderPage
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent
import java.io.File
import kotlinx.coroutines.delay

private const val LINE_REVEAL_MS = 500L
private const val KEN_BURNS_HALF_CYCLE_MS = 13_000
private const val KEN_BURNS_MAX_SCALE = 1.08f

@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel = hiltViewModel(),
    onContinue: (level: Int, clearTimeMs: Long) -> Unit,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mode = viewModel.mode
    val isLibrary = mode is ChapterReaderMode.Library

    BackHandler(enabled = isLibrary, onBack = onClose)

    ChapterReaderMusicBed()

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
        ) {
            when (val state = uiState) {
                ChapterReaderViewModel.ChapterReaderUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = JourneyTimeAccent)
                    }
                }

                is ChapterReaderViewModel.ChapterReaderUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = Color(0xFFFF8A80))
                            Spacer(Modifier.height(16.dp))
                            MenuButton(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(48.dp),
                                text = stringResource(
                                    if (isLibrary) R.string.chapter_reader_close
                                    else R.string.chapter_reader_continue
                                ),
                                textColor = Color.White,
                                paddingValues = PaddingValues(),
                                contentScale = ContentScale.Fit,
                            ) {
                                when (mode) {
                                    is ChapterReaderMode.FirstClear ->
                                        onContinue(mode.level, mode.clearTimeMs)
                                    ChapterReaderMode.Library -> onClose()
                                }
                            }
                        }
                    }
                }

                is ChapterReaderViewModel.ChapterReaderUiState.Ready -> {
                    ChapterReaderContent(
                        page = state.page,
                        canGoPrevious = state.canGoPrevious,
                        canGoNext = state.canGoNext,
                        showContinue = !isLibrary,
                        onPrevious = viewModel::previousChapter,
                        onNext = viewModel::nextChapter,
                        onContinue = {
                            val firstClear = mode as? ChapterReaderMode.FirstClear ?: return@ChapterReaderContent
                            onContinue(firstClear.level, firstClear.clearTimeMs)
                        },
                        onClose = onClose,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterReaderContent(
    page: ChapterReaderPage,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    showContinue: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onContinue: () -> Unit,
    onClose: () -> Unit,
) {
    val backdrop = page.backdropFilePath.takeIf { it.isNotBlank() }?.let { File(it) }
    val kenBurns = rememberInfiniteTransition(label = "chapterBackdropZoom")
    val scale by kenBurns.animateFloat(
        initialValue = 1f,
        targetValue = KEN_BURNS_MAX_SCALE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = KEN_BURNS_HALF_CYCLE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "chapterBackdropScale",
    )

    Box(Modifier.fillMaxSize().clipToBounds()) {
        AsyncImage(
            model = backdrop,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0x99081014),
                            0.35f to Color(0x66081014),
                            0.65f to Color(0xB3081014),
                            1f to Color(0xF2081014),
                        ),
                    ),
                ),
        )

        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.chapter_reader_progress,
                        page.chapterIndex,
                        page.totalChapters,
                    ),
                    color = JourneyTimeAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                )
                if (!showContinue) {
                    TextButton(onClick = onClose) {
                        Text(
                            text = stringResource(R.string.chapter_reader_close),
                            color = JourneyTimeAccent,
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            ChapterBodyReveal(
                body = page.body,
                chapterKey = page.issueId to page.chapterIndex,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChapterNavButton(
                    label = stringResource(R.string.chapter_reader_previous),
                    enabled = canGoPrevious,
                    onClick = onPrevious,
                )
                ChapterNavButton(
                    label = stringResource(R.string.chapter_reader_next),
                    enabled = canGoNext,
                    onClick = onNext,
                )
            }
            if (showContinue) {
                Spacer(Modifier.height(4.dp))
                MenuButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.chapter_reader_continue),
                    textColor = Color.White,
                    paddingValues = PaddingValues(),
                    contentScale = ContentScale.FillBounds,
                    onClick = onContinue,
                )
            }
        }
    }
}

@Composable
private fun ChapterNavButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(
            text = label,
            color = if (enabled) JourneyTimeAccent else Color.White.copy(alpha = 0.28f),
        )
    }
}

@Composable
private fun ChapterBodyReveal(
    body: String,
    chapterKey: Pair<Int, Int>,
    modifier: Modifier = Modifier,
) {
    val lines = remember(chapterKey, body) { body.lines() }
    var revealedCount by remember(chapterKey) { mutableIntStateOf(0) }
    var skipReveal by remember(chapterKey) { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val tapSource = remember { MutableInteractionSource() }

    LaunchedEffect(chapterKey, skipReveal) {
        if (skipReveal || lines.isEmpty()) {
            revealedCount = lines.size
            return@LaunchedEffect
        }
        revealedCount = 0
        for (count in 1..lines.size) {
            delay(LINE_REVEAL_MS)
            revealedCount = count
        }
    }

    val visibleBody = remember(lines, revealedCount) {
        lines.take(revealedCount).joinToString("\n")
    }
    val fullyRevealed = revealedCount >= lines.size

    Text(
        text = visibleBody,
        color = Color(0xFFF4F1EB),
        fontSize = 18.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Normal,
        modifier = modifier
            .verticalScroll(scrollState)
            .clickable(
                interactionSource = tapSource,
                indication = null,
                enabled = !fullyRevealed,
            ) {
                skipReveal = true
            }
            .padding(vertical = 4.dp),
    )
}

package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
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
import kotlin.math.roundToInt

/** Continuous wipe speed (px / sec). Longer chapters take longer, clamped below. */
private const val REVEAL_PX_PER_SEC = 140f
private const val REVEAL_MIN_MS = 2_500
private const val REVEAL_MAX_MS = 14_000
private const val KEN_BURNS_HALF_CYCLE_MS = 13_000
private const val KEN_BURNS_MAX_SCALE = 1.08f

@Composable
fun ChapterReaderScreen(
    viewModel: ChapterReaderViewModel = hiltViewModel(),
    onContinue: (level: Int, clearTimeMs: Long) -> Unit,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val musicMuted by viewModel.musicMuted.collectAsStateWithLifecycle()
    val mode = viewModel.mode
    val isLibrary = mode is ChapterReaderMode.Library

    BackHandler(enabled = isLibrary, onBack = onClose)

    ChapterReaderMusicBed(muted = musicMuted)

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
                        showRating = state.showRating,
                        myStars = state.myStars,
                        musicMuted = musicMuted,
                        onToggleMute = viewModel::toggleMusicMuted,
                        onPrevious = viewModel::previousChapter,
                        onNext = viewModel::nextChapter,
                        onRate = viewModel::rateIssue,
                        onContinue = {
                            val firstClear = mode as? ChapterReaderMode.FirstClear
                                ?: return@ChapterReaderContent
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
    showRating: Boolean,
    myStars: Int?,
    musicMuted: Boolean,
    onToggleMute: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRate: (Int) -> Unit,
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
                // End padding is tighter so the chapter scrollbar can sit in that gutter.
                .padding(start = 20.dp, end = 10.dp, top = 12.dp, bottom = 12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
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
                    if (page.chapterTitle.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = page.chapterTitle,
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                        )
                    }
                }
                Icon(
                    painter = painterResource(
                        if (musicMuted) R.drawable.ic_story_music_off
                        else R.drawable.ic_story_music_on
                    ),
                    contentDescription = stringResource(
                        if (musicMuted) R.string.chapter_reader_unmute
                        else R.string.chapter_reader_mute
                    ),
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onToggleMute)
                        .padding(8.dp),
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
            Spacer(Modifier.height(16.dp))
            ChapterBodyReveal(
                body = page.body,
                chapterKey = page.issueId to page.chapterIndex,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            if (showRating) {
                Spacer(Modifier.height(10.dp))
                IssueRatingBar(
                    stars = myStars,
                    onRate = onRate,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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
    var textHeightPx by remember(chapterKey) { mutableFloatStateOf(0f) }
    var skipReveal by remember(chapterKey) { mutableStateOf(false) }
    val revealHeight = remember(chapterKey) { Animatable(0f) }
    val clipBottom = revealHeight.value
    val scrollState = rememberScrollState()
    val tapSource = remember { MutableInteractionSource() }
    val fullyRevealed = textHeightPx > 0f && clipBottom >= textHeightPx - 0.5f
    val scrollDescription = stringResource(R.string.chapter_reader_scroll_cd)

    LaunchedEffect(chapterKey, textHeightPx, skipReveal) {
        if (textHeightPx <= 0f) return@LaunchedEffect
        if (skipReveal) {
            revealHeight.snapTo(textHeightPx)
            return@LaunchedEffect
        }
        revealHeight.snapTo(0f)
        val durationMs = ((textHeightPx / REVEAL_PX_PER_SEC) * 1_000f)
            .roundToInt()
            .coerceIn(REVEAL_MIN_MS, REVEAL_MAX_MS)
        revealHeight.animateTo(
            targetValue = textHeightPx,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing),
        )
    }

    Box(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(end = CHAPTER_SCROLLBAR_GUTTER)
                .verticalScroll(scrollState)
                .clickable(
                    interactionSource = tapSource,
                    indication = null,
                    enabled = !fullyRevealed,
                ) {
                    skipReveal = true
                },
        ) {
            Text(
                text = body,
                color = Color(0xFFF4F1EB),
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                onTextLayout = { layout: TextLayoutResult ->
                    val height = layout.size.height.toFloat()
                    if (kotlin.math.abs(height - textHeightPx) > 0.5f) {
                        textHeightPx = height
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .drawWithContent {
                        val drawFullContent = this::drawContent
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.width,
                            bottom = clipBottom.coerceIn(0f, size.height),
                        ) {
                            drawFullContent()
                        }
                    },
            )
        }
        ChapterScrollBar(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(CHAPTER_SCROLLBAR_GUTTER)
                .semantics { contentDescription = scrollDescription },
        )
    }
}

/** Tiny always-visible scrollbar when chapter prose overflows the viewport. */
@Composable
private fun ChapterScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var viewportHeightPx by remember { mutableFloatStateOf(0f) }
    val minThumbPx = with(density) { 20.dp.toPx() }
    val trackInsetPx = with(density) { 4.dp.toPx() }
    val thumbWidthPx = with(density) { 3.dp.toPx() }

    Canvas(
        modifier
            .padding(vertical = 4.dp)
            .onSizeChanged { viewportHeightPx = it.height.toFloat() },
    ) {
        // Read inside draw so scroll updates redraw without full recomposition.
        val scrollMax = scrollState.maxValue
        val scrollValue = scrollState.value
        if (scrollMax <= 0 || viewportHeightPx <= 0f) return@Canvas
        val trackHeight = (size.height - trackInsetPx * 2f).coerceAtLeast(0f)
        if (trackHeight <= 0f) return@Canvas

        val contentHeight = viewportHeightPx + scrollMax
        val thumbHeight = (trackHeight * (viewportHeightPx / contentHeight))
            .coerceIn(minThumbPx, trackHeight)
        val travel = (trackHeight - thumbHeight).coerceAtLeast(0f)
        val thumbTop = trackInsetPx + (scrollValue.toFloat() / scrollMax.toFloat()) * travel
        val thumbLeft = (size.width - thumbWidthPx) / 2f
        val corner = CornerRadius(thumbWidthPx / 2f, thumbWidthPx / 2f)

        drawRoundRect(
            color = Color.White.copy(alpha = 0.14f),
            topLeft = Offset(thumbLeft, trackInsetPx),
            size = Size(thumbWidthPx, trackHeight),
            cornerRadius = corner,
        )
        drawRoundRect(
            color = JourneyTimeAccent.copy(alpha = 0.9f),
            topLeft = Offset(thumbLeft, thumbTop),
            size = Size(thumbWidthPx, thumbHeight),
            cornerRadius = corner,
        )
    }
}

private val CHAPTER_SCROLLBAR_GUTTER = 10.dp

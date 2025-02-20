package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.game

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.JourneyGameModel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.ErrorOrEmptyContent
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoadingContent
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyButtonBackground
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun JourneyGameScreen(
    modifier: Modifier = Modifier, viewModel: JourneyGameViewModel = hiltViewModel()
) {

    val journeyState by viewModel.journeyDataState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.gameEvent.collect { gameEvent ->
            when (gameEvent) {
                JourneyGameEvent.Correct -> {
                    Toast.makeText(context, "CORRECT!", Toast.LENGTH_LONG).show()
                }

                JourneyGameEvent.NotEnoughSelected -> {
                    Toast.makeText(context, "Not enough sounds selected!", Toast.LENGTH_LONG).show()
                }

                JourneyGameEvent.TooManySelected -> {
                    Toast.makeText(context, "Too many sounds selected!", Toast.LENGTH_LONG).show()
                }

                JourneyGameEvent.Wrong -> {
                    Toast.makeText(context, "WRONG!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.journey_game_bg2),
                        contentScale = ContentScale.Crop
                    ),

                ) {
                JourneyGameContent(journeyState, viewModel)
            }
        })
}

@Composable
fun JourneyGameContent(journeyState: JourneyLevelFetchState, viewModel: JourneyGameViewModel) {
    when (journeyState) {
        JourneyLevelFetchState.Loading -> {
            LoadingContent()
        }

        is JourneyLevelFetchState.Error -> {
            ErrorOrEmptyContent(journeyState.error)
        }

        is JourneyLevelFetchState.Success -> {
            JourneyGameData(journeyState.data, viewModel)
        }
    }
}

@Composable
fun JourneyGameData(journeyState: JourneyGameModel, viewModel: JourneyGameViewModel) {
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val imageRowPadding = 8.dp
    val imageSize =
        ((currentScreenWidth - 2 * imageRowPadding.value) / journeyState.radiantHeroImages.size).coerceAtMost(
            150f
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SoundCountRow(
            level = journeyState.level,
            viewModel = viewModel,
            totalCorrectSounds = journeyState.totalCorrectSounds
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = imageRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            journeyState.radiantHeroImages.forEach { img ->
                Image(
                    painterResource(img),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(imageSize.dp),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = JourneyButtonBackground
            ),
            border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.7f)),
            modifier = Modifier
                .size(120.dp, 60.dp)
                .clickable {
                    viewModel.submitAnswer()
                }
                .shadow(
                    elevation = 30.dp,
                    ambientColor = Color.Blue,
                    spotColor = Color.Cyan,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Submit",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            items(items = journeyState.soundList, key = { it.soundModel.id }) { sound ->

                SoundCard(selectedState = viewModel.selectedSoundState.getValue(sound.soundModel.id),
                    onCardClicked = {
                        viewModel.toggleSoundCardState(sound.soundModel.id)
                    },
                    onSoundIconClicked = {
                        viewModel.playSound(sound.soundModel)
                    })
            }
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context, currentScreenWidth
                    ).height.dp
                )
        ) {
            AndroidView(
                // on below line specifying width for ads.
                modifier = Modifier.fillMaxWidth(), factory = { context ->
                    // on below line specifying ad view.
                    AdView(context).apply {
                        // on below line specifying ad size
                        setAdSize(
                            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                context, currentScreenWidth
                            )
                        )
                        // on below line specifying ad unit id
                        // currently added a test ad unit id.
                        adUnitId = context.getString(R.string.banner_id)
                        // calling load ad to load our ad.
                        loadAd(AdRequest.Builder().build())
                    }
                })
        }
    }
}

@Composable
fun SoundCountRow(level: Int, viewModel: JourneyGameViewModel, totalCorrectSounds: Int) {
    val selectedSounds = viewModel.selectedSoundState.values.count { x -> x == true }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Level: $level",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            stringResource(
                R.string.sounds_selected_total, selectedSounds, totalCorrectSounds
            ),
            color = if (selectedSounds > totalCorrectSounds) Color.Red.copy(alpha = 0.7f) else Color.White,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
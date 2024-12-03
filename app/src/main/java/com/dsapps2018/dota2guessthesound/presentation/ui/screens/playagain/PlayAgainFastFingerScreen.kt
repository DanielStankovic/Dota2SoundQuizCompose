package com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@Composable
fun PlayAgainFastFingerScreen(
    modifier: Modifier = Modifier,
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    scoreGuessed: Int,
    scoreTotal: Int,
    time: Int,
    answeredAll: Boolean,
    onPlayAgain: () -> Unit
) {
    scoreViewModel.updateFastFingerScore(scoreGuessed, scoreTotal, time)
    val userData by scoreViewModel.userData.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.quiz_background),
                        contentScale = ContentScale.Crop
                    ),

                ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        stringResource(
                            R.string.highscore_fast_finger, time, when (time) {
                                30 -> userData.thirtySecondsScore
                                60 -> userData.sixtySecondsScore
                                90 -> userData.ninetySecondsScore
                                else -> userData.thirtySecondsScore
                            }
                        ),
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(100.dp))

                    Text(
                        stringResource(R.string.you_score_lbl),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.score_fast_finger_calculated, scoreGuessed, scoreTotal, scoreViewModel.calculateFastFingerScore(scoreGuessed, scoreTotal)),
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.time_picked, time),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        color = Color.White
                    )

                    if (answeredAll) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            stringResource(R.string.no_more_sounds_msg),
                            textAlign = TextAlign.Center,
                            fontSize = 26.sp,
                            color = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))

                    MenuButton(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(200.dp),
                        paddingValues = PaddingValues(),
                        text = stringResource(R.string.play_again_lbl), textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        onPlayAgain()
                    }
                }

            }
        }
    )
}
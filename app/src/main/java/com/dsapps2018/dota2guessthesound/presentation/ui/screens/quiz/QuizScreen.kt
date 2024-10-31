package com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.AnimatedImages
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import kotlin.random.Random

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    quizViewModel: QuizViewModel = hiltViewModel(),
    onPlayAgain: (Int, Boolean) -> Unit
) {

    val context = LocalContext.current
    val animationTrigger by quizViewModel.triggerAnimation.collectAsStateWithLifecycle()

    var buttonOptionsList: List<String> by remember {
        mutableStateOf(listOf("", "", "", ""))
    }
    var score: Int by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        quizViewModel.quizEvent.collect { quizEvent ->
            when (quizEvent) {
                is QuizEventState.SoundReady -> {
                    buttonOptionsList = quizEvent.buttonOptions
                }

                QuizEventState.CorrectSound -> {
                    score++
                    quizViewModel.triggerImageAnimation()
                }

                QuizEventState.WrongSound -> {
                    showInterstitial(context){
                        onPlayAgain(score, false)
                    }
                }

                QuizEventState.NoMoreSounds -> {
                    showInterstitial(context){
                        onPlayAgain(score, true)
                    }
                }
            }
        }
    }
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
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp, end = 40.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Score: $score", fontSize = 30.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    AnimatedImages(
                        modifier = Modifier.size(150.dp),
                        bigImage = painterResource(id = R.drawable.sound_button_cut),
                        smallImage = painterResource(id = R.drawable.gj),
                        onImageClick = {
                            quizViewModel.playSound()
                        },
                        animationTrigger = animationTrigger,
                        resetAnimationTrigger = {
                            quizViewModel.resetAnimationTrigger()
                        },
                        floatOffset = if (Random.nextBoolean()) (-130f) else (150f)

                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[0],
                            textColor = Color.White,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[0])
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[1],
                            textColor = Color.White,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[1])
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,

                        ) {
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[2],
                            textColor = Color.White,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[2])
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[3],
                            textColor = Color.White,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[3])
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.6f))
                }

            }
        }
    )
}
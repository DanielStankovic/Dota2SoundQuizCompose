package com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.admob.showInterstitial
import com.dsapps2018.dota2guessthesound.data.admob.showRewardedAd
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.toDp
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.AnimatedImages
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.SingleOptionDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.WatchAdContinueDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.random.Random

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    quizViewModel: QuizViewModel = hiltViewModel(),
    onPlayAgain: (Int, Boolean) -> Unit
) {

    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val animationTrigger by quizViewModel.triggerAnimation.collectAsStateWithLifecycle()
    val isRewardedReady by isAdReady.collectAsStateWithLifecycle()

    var buttonOptionsList: List<String> by remember {
        mutableStateOf(
            listOf(
                Constants.EMPTY_STRING,
                Constants.EMPTY_STRING,
                Constants.EMPTY_STRING,
                Constants.EMPTY_STRING
            )
        )
    }
    var score: Int by remember {
        mutableIntStateOf(0)
    }

    var showDialog by remember { mutableStateOf(false) }
    var showWatchAdContinueDialog by remember { mutableStateOf(false) }

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
                    if (!quizViewModel.additionalLifeUsed && isRewardedReady) {
                        showWatchAdContinueDialog = true
                    } else {
                        showInterstitial(context) {
                            onPlayAgain(score, false)
                        }
                    }
                }

                QuizEventState.NoMoreSounds -> {
                    showInterstitial(context) {
                        onPlayAgain(score, true)
                    }
                }

                QuizEventState.ConnectionLost -> {
                    showDialog = true
                }
            }
        }
    }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            if (showDialog) SingleOptionDialog(
                onDismiss = {
                    showDialog = false
                },
                onOptionClick = {
                    showDialog = false
                },
                titleText = context.getString(R.string.error_connection_lost_title),
                messageText = context.getString(R.string.error_connection_lost_msg),
                optionText = context.getString(R.string.lbl_ok),
                dismissible = false
            )

            if (showWatchAdContinueDialog) WatchAdContinueDialog(
                onDismiss = {
                    showWatchAdContinueDialog = false
                },
                onSkipClicked = {
                    showWatchAdContinueDialog = false
                    showInterstitial(context) {
                        onPlayAgain(score, false)
                    }
                },
                onWatchAdClicked = {
                    showWatchAdContinueDialog = false
                    showRewardedAd(context, onRewarded = {
                        //If we played sound here the sound would play while the
                        //ad is still fully visible which is bad. This way, we just set the flag
                        //and check this flag onAdDismissed which is triggered when we close the ad.
                        //Back button does not work when ad is fully visible, so user can not
                        //exit the ad and trigger onPlayAgain, but we still include it to safe guard.
                        quizViewModel.additionalLifeUsed = true

                    }, onAdDismissed = {
                        if (quizViewModel.additionalLifeUsed) {
                            quizViewModel.generateAndPlaySound()
                        } else {
                            onPlayAgain(score, false)
                        }
                    })
                }
            )

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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, end = 40.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            context.getString(R.string.score, score),
                            fontSize = 30.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(70.dp))

                    AnimatedImages(
                        modifier = Modifier.size(420.toDp()),
                        bigImage = painterResource(id = R.drawable.sound_button),
                        smallImageCorrect = painterResource(id = R.drawable.gj),
                        smallImageWrong = painterResource(id = R.drawable.wrong),
                        onImageClick = {
                            quizViewModel.playSound()
                        },
                        animationTriggerCorrect = animationTrigger,
                        resetAnimationTriggerCorrect = {
                            quizViewModel.resetAnimationTrigger()
                        },
                        floatOffsetCorrect = if (Random.nextBoolean()) (-360f) else (400f)

                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.toDp()),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[0],
                            textColor = Color.White,
                            maxLines = 2,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[0])
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.toDp()),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[1],
                            maxLines = 2,
                            textColor = Color.White,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[1])
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,

                        ) {
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.toDp()),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[2],
                            textColor = Color.White,
                            maxLines = 2,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[2])
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        MenuButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.toDp()),
                            paddingValues = PaddingValues(),
                            text = buttonOptionsList[3],
                            textColor = Color.White,
                            maxLines = 2,
                            contentScale = ContentScale.FillBounds
                        ) {
                            quizViewModel.onAnswerClicked(buttonOptionsList[3])
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .fillMaxWidth()
                            .height(
                                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                    context, currentScreenWidth
                                ).height.dp
                            )
                    ) {
                        AndroidView(
                            // on below line specifying width for ads.
                            factory = { context ->
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
        }
    )
}
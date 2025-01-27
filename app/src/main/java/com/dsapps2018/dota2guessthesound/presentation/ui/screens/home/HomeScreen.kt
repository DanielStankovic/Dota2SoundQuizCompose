package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
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
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.openDiscordInviteLink
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.AnimatedIcon
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoginStatusComposable
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.PermissionDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.coininfo.CoinInfoDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile.AuthEvent
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile.AuthViewModel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.github.jan.supabase.auth.status.SessionStatus

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onQuizClicked: () -> Unit,
    onFastFingerClicked: () -> Unit,
    onInvokerClicked: () -> Unit,
    onJokeClicked: () -> Unit,
    onOptionsClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onQuestionClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authStatus.collectAsStateWithLifecycle()
    val userData by authViewModel.userData.collectAsStateWithLifecycle()
    val lastSyncDate by authViewModel.modifiedDateFlow.collectAsStateWithLifecycle()
    val currentIndex by homeViewModel.currentIndex.collectAsStateWithLifecycle()
    val isRewardedReady by isAdReady.collectAsStateWithLifecycle()

    var targetRotation by remember { mutableFloatStateOf(0f) }
    var isFlippingForward by remember { mutableStateOf(true) }
    var shouldAnimate by remember { mutableStateOf(false) }
    var showCoinInfoDialog by remember { mutableStateOf(false) }


    val animatedRotationY by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 500),
        finishedListener = { shouldAnimate = false },
        label = "RotatingButton"
    )

    LaunchedEffect(currentIndex) {
        if (shouldAnimate) {
            targetRotation = if (isFlippingForward) targetRotation + 360f else targetRotation - 360f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulsatingButton")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsatingButton"
    )

    // Define the alpha animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.8f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulsatingButton"
    )

    LaunchedEffect(Unit) {
        authViewModel.authEventStatus.collect { authEvent ->
            when (authEvent) {
                is AuthEvent.Success -> {
                    snackbarHostState.showSnackbar(authEvent.message)
                }

                is AuthEvent.Error -> {
                    if (!authEvent.isDismissible) {
                        snackbarHostState.showSnackbar(
                            message = authEvent.error,
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            message = authEvent.error
                        )
                    }
                }
            }
        }
    }


    Scaffold(contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState, modifier = Modifier.padding(
                    bottom = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context, currentScreenWidth
                    ).height.dp
                )
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DialogBackground,
                    contentColor = Color.White,
                    dismissActionContentColor = Color.White
                )
            }
        },
        content = { padding ->
            if (showCoinInfoDialog) {
                CoinInfoDialog {
                    showCoinInfoDialog = false
                }
            }
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.home_background),
                        contentScale = ContentScale.Crop
                    )

            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, end = 30.dp, start = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoginStatusComposable(
                            sessionStatus = authState,
                            lastSyncString = lastSyncDate,
                            noncePair = authViewModel.getNoncePair(),
                            onUserImageClick = {
                                onProfileClicked()
                            },
                            onSignInToSupabase = { googleIdToken, rawNonce ->
                                authViewModel.signInToSupabase(googleIdToken, rawNonce)
                            },
                            onLoginError = { e ->
                                authViewModel.onErrorException(e)
                            },
                            signInButtonModifier = Modifier
                                .wrapContentWidth()
                                .height(50.dp),
                            profileImageSize = 40.dp,
                            showLoginLabel = false,
                            showSyncLabel = false
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_cogwheel),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onOptionsClicked()
                                },
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp, start = 30.dp, end = 30.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = painterResource(if (authState is SessionStatus.Authenticated) R.drawable.leaderboard else R.drawable.leaderboard_disabled),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    if (authState is SessionStatus.Authenticated) {
                                        onLeaderboardClicked()
                                    } else {
                                        authViewModel.onErrorEvent(context.getString(R.string.leaderboard_login_required))
                                    }
                                })
                        Spacer(Modifier.weight(1f))
                        AnimatedIcon(currentIndex)

                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    ) {
                        when (currentIndex) {
                            0 -> {
                                HomeQuizComposable(currentIndex = currentIndex,
                                    animatedRotationY = animatedRotationY,
                                    setShouldAnimate = { boolValue ->
                                        shouldAnimate = boolValue
                                    },
                                    setIsFlippingForward = { boolValue ->
                                        isFlippingForward = boolValue
                                    },
                                    onMoveLeft = {
                                        homeViewModel.moveLeft()
                                    },
                                    onMoveRight = {
                                        homeViewModel.moveRight()
                                    },
                                    onQuizClicked = {
                                        onQuizClicked()
                                    },
                                    onMoveToJokeScreen = {
                                        homeViewModel.moveToJokeIndex()
                                    })
                            }

                            1 -> {
                                HomeFastFingerComposable(currentIndex = currentIndex,
                                    animatedRotationY = animatedRotationY,
                                    setShouldAnimate = { boolValue ->
                                        shouldAnimate = boolValue
                                    },
                                    setIsFlippingForward = { boolValue ->
                                        isFlippingForward = boolValue
                                    },
                                    onMoveLeft = {
                                        homeViewModel.moveLeft()
                                    },
                                    onMoveRight = {
                                        homeViewModel.moveRight()
                                    },
                                    onFastFingerClicked = {
                                        onFastFingerClicked()
                                    },
                                    onMoveToJokeScreen = {
                                        homeViewModel.moveToJokeIndex()
                                    })
                            }

                            2 -> {
                                HomeInvokerComposable(currentIndex = currentIndex,
                                    animatedRotationY = animatedRotationY,
                                    isRewardedReady = isRewardedReady,
                                    userCoinValue = userData.coinValue,
                                    scale = scale,
                                    alpha = alpha,
                                    setShouldAnimate = { boolValue ->
                                        shouldAnimate = boolValue
                                    },
                                    setIsFlippingForward = { boolValue ->
                                        isFlippingForward = boolValue
                                    },
                                    setShowCoinInfoDialog = { boolValue ->
                                        showCoinInfoDialog = boolValue
                                    },
                                    onMoveLeft = {
                                        homeViewModel.moveLeft()
                                    },
                                    onMoveRight = {
                                        homeViewModel.moveRight()
                                    },
                                    onInvokerClicked = {
                                        onInvokerClicked()
                                    },
                                    updateCoinValue = { value ->
                                        authViewModel.updateCoinValue(
                                            value
                                        )
                                    }

                                )
                            }

                            3 -> {
                                HomeJokeComposable(currentIndex = currentIndex,
                                    animatedRotationY = animatedRotationY,
                                    isRewardedReady = isRewardedReady,
                                    scale = scale,
                                    alpha = alpha,
                                    setShouldAnimate = { boolValue ->
                                        shouldAnimate = boolValue
                                    },
                                    setIsFlippingForward = { boolValue ->
                                        isFlippingForward = boolValue
                                    },
                                    setShowCoinInfoDialog = { boolValue ->
                                        showCoinInfoDialog = boolValue
                                    },
                                    onMoveLeft = {
                                        homeViewModel.moveLeft()
                                    },
                                    onMoveRight = {
                                        homeViewModel.moveRight()
                                    },
                                    onJokeClicked = {
                                        onJokeClicked()
                                    },
                                    updateJokeCoinValue = { value ->

                                    })
                            }
                        }


                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_question),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .size(40.dp)
                                .clickable {
                                    onQuestionClicked()
                                })
                        Spacer(Modifier.weight(1f))
                        Column(
                            modifier = Modifier
                                .padding(bottom = 30.dp, end = 30.dp)
                                .clickable {
                                    openDiscordInviteLink(context, Constants.DISCORD_INV_LINK)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(R.string.join_lbl),
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(R.drawable.ic_discord),
                                contentDescription = null,
                                tint = Color.White
                            )
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                HandlePermissionRequest(setPermissionCheckTimestamp = {
                    homeViewModel.setPermissionCheckTimestamp()
                }, shouldShowRationaleAgain = {
                    homeViewModel.shouldShowRationaleAgain()
                })
            }
        })

}


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun HandlePermissionRequest(
    setPermissionCheckTimestamp: () -> Unit, shouldShowRationaleAgain: () -> Boolean
) {
    var showDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    when (val status = permissionState.status) {
        is PermissionStatus.Granted -> {}
        is PermissionStatus.Denied -> if (shouldShowRationaleAgain()) {
            if (showDialog) PermissionDialog(onDismiss = {
                setPermissionCheckTimestamp()
                showDialog = false
            },
                showRationale = status.shouldShowRationale,
                onOkClick = { permissionState.launchPermissionRequest() },
                onGoToSettings = {
                    val packageName = context.packageName
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", packageName, null)
                    launcher.launch(intent)
                })
        }
    }
}

@Composable
fun ArrowButton(isRightArrow: Boolean, currentIndex: Int, onClick: () -> Unit) {
    Image(painter = painterResource(
        if (isArrowEnabled(
                isRightArrow, currentIndex
            )
        ) R.drawable.arrow_enabled else R.drawable.arrow_disabled
    ),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .rotate(if (isRightArrow) 180f else 0f)
            .then(if (isArrowEnabled(isRightArrow, currentIndex)) {
                Modifier.clickable {
                    onClick()
                }
            } else {
                Modifier
            }))
}

fun isArrowEnabled(isRightArrow: Boolean, currentIndex: Int): Boolean {
    return if (isRightArrow) {
        currentIndex < 3
    } else {
        currentIndex > 0
    }
}



package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.admob.isAdReady
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.FeatureFlags
import com.dsapps2018.dota2guessthesound.data.util.findActivity
import com.dsapps2018.dota2guessthesound.data.util.openDiscordInviteLink
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.BannerView
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
    modifier: Modifier = Modifier,
    onQuizClicked: () -> Unit,
    onFastFingerClicked: () -> Unit,
    onJourneyClicked: () -> Unit,
    onReadingLocaleClicked: () -> Unit,
    onInvokerClicked: () -> Unit,
    onJokeClicked: () -> Unit,
    onHeroCatalogClicked: () -> Unit,
    onOptionsClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onQuestionClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onUpdateRequired: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authStatus.collectAsStateWithLifecycle()
    val progress by homeViewModel.progress.collectAsStateWithLifecycle()
    val lastSyncDate by homeViewModel.lastSyncDate.collectAsStateWithLifecycle()
    val currentIndex by homeViewModel.currentIndex.collectAsStateWithLifecycle()
    val lastPageIndex = homeViewModel.lastPageIndex
    val isRewardedReady by isAdReady.collectAsStateWithLifecycle()
    val updateRequiredStatus by homeViewModel.updateRequiredStatus.collectAsStateWithLifecycle()
    val coinAdGrantStatus by homeViewModel.coinAdGrantStatus.collectAsStateWithLifecycle()
    var targetRotation by remember { mutableFloatStateOf(0f) }
    var isFlippingForward by remember { mutableStateOf(true) }
    var shouldAnimate by remember { mutableStateOf(false) }
    var showCoinInfoDialog by remember { mutableStateOf(false) }

    if (updateRequiredStatus) {
        onUpdateRequired()
    }

    val animatedRotationY by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 500),
        finishedListener = { shouldAnimate = false },
        label = "RotatingButton"
    )


    BackHandler {
        context.findActivity()?.finish()
    }

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
        homeViewModel.journeyEntry.collect { entry ->
            when (entry) {
                HomeViewModel.JourneyEntry.ReadingLocale -> onReadingLocaleClicked()
                HomeViewModel.JourneyEntry.Levels -> onJourneyClicked()
            }
        }
    }

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

    LaunchedEffect(true) {
        homeViewModel.checkForcedVersion()
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
                CoinInfoDialog(
                    onDismiss = { showCoinInfoDialog = false },
                )
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
                        ArcadeLeaderboardHomeButton(
                            enabled = FeatureFlags.ARCADE_LEADERBOARD_ENABLED,
                            isAuthenticated = authState is SessionStatus.Authenticated,
                            onLeaderboardClicked = onLeaderboardClicked,
                            onLoginRequired = { message ->
                                authViewModel.onErrorEvent(message)
                            },
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            progress.coinValue.toString(),
                            color = Color.White,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(12.dp))
                        Image(painter = painterResource(R.drawable.coin_blank),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    showCoinInfoDialog = true
                                })
                        //TODO Ovo za kasnije implementacija sa fadein/faceout
//                        AnimatedIcon(currentIndex)

                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                    ) {
                        when (homeViewModel.pages.getOrNull(currentIndex)) {
                            HomeCarouselPage.HeroCatalog -> {
                                HomeHeroCatalogComposable(
                                    currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
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
                                    onHeroCatalogClicked = onHeroCatalogClicked,
                                )
                            }

                            HomeCarouselPage.Quiz -> {
                                HomeQuizComposable(currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
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
                                        homeViewModel.onQuizSelected()
                                        onQuizClicked()
                                    },
                                    onMoveToJokeScreen = {
                                        homeViewModel.moveToJokeIndex()
                                    })
                            }

                            HomeCarouselPage.FastFinger -> {
                                HomeFastFingerComposable(currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
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
                                        homeViewModel.onFastFingerSelected()
                                        onFastFingerClicked()
                                    },
                                    onMoveToJokeScreen = {
                                        homeViewModel.moveToJokeIndex()
                                    })
                            }

                            HomeCarouselPage.Journey -> {
                                HomeJourneyComposable(currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
                                    isLoggedIn = authState is SessionStatus.Authenticated ,
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
                                    onJourneyClicked = {
                                        homeViewModel.requestJourneyEntry()
                                    },
                                    onJourneyGated = {
                                        homeViewModel.onJourneyGated()
                                    })
                            }

                            HomeCarouselPage.Invoker -> {
                                LaunchedEffect(Unit) {
                                    homeViewModel.refreshCoinAdGrantStatus()
                                }
                                HomeInvokerComposable(
                                    currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
                                    animatedRotationY = animatedRotationY,
                                    isRewardedReady = isRewardedReady,
                                    canEnterInvoker = homeViewModel.canEnterInvoker(progress.coinValue),
                                    coinAdGrant = coinAdGrantStatus,
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
                                    onGrantCoinsFromAd = {
                                        homeViewModel.grantCoinsFromAd()
                                    },
                                    onRefreshCoinAdGrant = {
                                        homeViewModel.refreshCoinAdGrantStatus()
                                    },
                                )
                            }

                            HomeCarouselPage.Joke -> {
                                HomeJokeComposable(currentIndex = currentIndex,
                                    lastPageIndex = lastPageIndex,
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
                                    onJokeSoundClicked = {
                                        homeViewModel.playJokeSound()
                                    },
                                    updateJokeCoinValue = { value ->

                                    })
                            }

                            null -> {}
                        }


                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (FeatureFlags.ARCADE_LEADERBOARD_ENABLED) {
                            Icon(
                                painter = painterResource(R.drawable.ic_question),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .size(40.dp)
                                    .clickable {
                                        onQuestionClicked()
                                    },
                            )
                        }
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
                    BannerView()
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


@Composable
private fun ArcadeLeaderboardHomeButton(
    enabled: Boolean,
    isAuthenticated: Boolean,
    onLeaderboardClicked: () -> Unit,
    onLoginRequired: (String) -> Unit,
) {
    if (!enabled) return
    val loginRequiredMessage = stringResource(R.string.leaderboard_login_required)
    Image(
        painter = painterResource(
            if (isAuthenticated) R.drawable.leaderboard else R.drawable.leaderboard_disabled
        ),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clickable {
                if (isAuthenticated) {
                    onLeaderboardClicked()
                } else {
                    onLoginRequired(loginRequiredMessage)
                }
            },
    )
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
fun ArrowButton(isRightArrow: Boolean, currentIndex: Int, lastPageIndex: Int, onClick: () -> Unit) {
    Image(painter = painterResource(
        if (isArrowEnabled(
                isRightArrow, currentIndex, lastPageIndex
            )
        ) R.drawable.arrow_enabled else R.drawable.arrow_disabled
    ),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .rotate(if (isRightArrow) 180f else 0f)
            .then(if (isArrowEnabled(isRightArrow, currentIndex, lastPageIndex)) {
                Modifier.clickable {
                    onClick()
                }
            } else {
                Modifier
            }))
}

fun isArrowEnabled(isRightArrow: Boolean, currentIndex: Int, lastPageIndex: Int): Boolean {
    return if (isRightArrow) {
        currentIndex < lastPageIndex
    } else {
        currentIndex > 0
    }
}



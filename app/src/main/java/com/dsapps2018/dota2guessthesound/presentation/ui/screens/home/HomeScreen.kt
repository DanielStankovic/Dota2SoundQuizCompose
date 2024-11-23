package com.dsapps2018.dota2guessthesound.presentation.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.PermissionDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun HomeScreen(
    onQuizClicked: () -> Unit,
    onFastFingerClicked: () -> Unit,
    onInvokerClicked: () -> Unit,
    onOptionsClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val window = (LocalView.current.context as Activity).window
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    DisposableEffect(Unit) {
        window.navigationBarColor = Color.Black.toArgb()
        onDispose {
            window.navigationBarColor = Color.Transparent.toArgb()
        }

    }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
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
                    ),

                ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 100.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(150.dp))

                    MenuButton(
                        modifier = Modifier.wrapContentHeight(),
                        paddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        maxLines = 1,
                        text = stringResource(R.string.quiz_lbl), textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        onQuizClicked()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    MenuButton(
                        modifier = Modifier.wrapContentHeight(),
                        paddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        maxLines = 1,
                        text = stringResource(R.string.fast_finger_lbl), textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        onFastFingerClicked()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    MenuButton(
                        modifier = Modifier.wrapContentHeight(),
                        paddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        maxLines = 1,
                        text = stringResource(R.string.invoker_lbl), textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        onInvokerClicked()
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 100.dp, end = 50.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cogwheel),
                        modifier = Modifier.size(50.dp).clickable{
                            onOptionsClicked()
                        },
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
                AndroidView(
                    // on below line specifying width for ads.
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    factory = { context ->
                        // on below line specifying ad view.
                        AdView(context).apply {
                            // on below line specifying ad size
                            setAdSize(
                                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                    context,
                                    currentScreenWidth
                                )
                            )
                            // on below line specifying ad unit id
                            // currently added a test ad unit id.
                            adUnitId = context.getString(R.string.banner_id)
                            // calling load ad to load our ad.
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                HandlePermissionRequest(
                    setPermissionCheckTimestamp = {
                        homeViewModel.setPermissionCheckTimestamp()
                    },
                    shouldShowRationaleAgain = {
                        homeViewModel.shouldShowRationaleAgain()
                    }
                )
            }
        }
    )

}


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun HandlePermissionRequest(
    setPermissionCheckTimestamp: () -> Unit,
    shouldShowRationaleAgain: () -> Boolean
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
            if (showDialog) PermissionDialog(
                onDismiss = {
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
                }
            )
        }
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.model.PlayerProgress
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoginStatusComposable
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.OptionsItem
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.SingleOptionDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authStatus.collectAsStateWithLifecycle()
    val progress by authViewModel.progress.collectAsStateWithLifecycle()
    val lastSyncDate by authViewModel.modifiedDateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    val filterLabels = remember {
        listOf("Quiz", "FF-30", "FF-60", "FF-90", "Invoker")
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }


    LaunchedEffect(Unit) {
        authViewModel.authEventStatus.collect { authEvent ->
            when (authEvent) {
                is AuthEvent.Success -> {
                    snackbarHostState.showSnackbar(authEvent.message)
                }

                is AuthEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = authEvent.error,
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DialogBackground,
                    contentColor = Color.White,
                    dismissActionContentColor = Color.White
                )
            }
        },
        content = { padding ->
            if (showDialog) SingleOptionDialog(
                onDismiss = {
                    showDialog = false
                },
                onOptionClick = {
                    showDialog = false
                    authViewModel.signOut()
                },
                titleText = stringResource(R.string.logout),
                messageText = stringResource(R.string.logout_msg),
                optionText = stringResource(R.string.logout),
                dismissible = true
            )

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
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .padding(top = 70.dp, bottom = 30.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    LoginStatusComposable(
                        sessionStatus = authState,
                        lastSyncString = lastSyncDate,
                        noncePair = authViewModel.getNoncePair(),
                        onSyncDataClick = {
                           authViewModel.syncUserData()
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
                        profileImageSize = 80.dp,
                        showLoginLabel = true,
                        showSyncLabel = true
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        filterLabels.forEachIndexed { thisIndex, filterLabel ->
                            FilterChip(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    selectedIndex = if (selectedIndex == thisIndex) {
                                        selectedIndex
                                    } else {
                                        thisIndex
                                    }
                                },
                                label = {
                                    Text(filterLabel)
                                },
                                selected = selectedIndex == thisIndex,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DialogOnBackground,
                                    labelColor = Color.White.copy(alpha = 0.8f),
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.8f),
                                    enabled = true,
                                    selected = selectedIndex == thisIndex
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))

                    UserDataComposable(selectedIndex, progress)

                    Spacer(modifier = Modifier.weight(1f))
                    if (authState is SessionStatus.Authenticated) {
                        OptionsItem(
                            modifier = Modifier.padding(horizontal = 40.dp),
                            leadingIcon = R.drawable.ic_logout,
                            trailingIcon = R.drawable.ic_arrow_right,
                            optionText = stringResource(R.string.logout)
                        ) {
                            showDialog = true
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun UserDataComposable(index: Int, progress: PlayerProgress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            "Times played\n\n${getTimesPlayed(index, progress)}",
            color = Color.White,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Text(
            "High score\n\n${getHighScore(index, progress)}",
            color = Color.White,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }

}

private fun getTimesPlayed(index: Int, progress: PlayerProgress): Int {
    return when (index) {
        0 -> progress.quizPlayed
        1 -> progress.thirtyPlayed
        2 -> progress.sixtyPlayed
        3 -> progress.ninetyPlayed
        4 -> progress.invokerPlayed
        else -> 0
    }
}

private fun getHighScore(index: Int, progress: PlayerProgress): Number {
    return when (index) {
        0 -> progress.quizScore
        1 -> progress.thirtySecondsScore
        2 -> progress.sixtySecondsScore
        3 -> progress.ninetySecondsScore
        4 -> progress.invokerScore
        else -> 0
    }
}
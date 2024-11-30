package com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.LoginStatusComposable
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.OptionsItem
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.dialog.SingleOptionDialog
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authStatus.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

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
                        .fillMaxHeight(0.7f)
                        .padding(top = 100.dp, bottom = 30.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    LoginStatusComposable(
                        sessionStatus = authState,
                        noncePair = authViewModel.getNoncePair(),
                        onUserImageClick = {
                            //Do Nothing
                        },
                        onSignInToSupabase = { googleIdToken, rawNonce ->
                            authViewModel.signInToSupabase(googleIdToken, rawNonce)
                        },
                        onLoginError = { e ->
                            authViewModel.onErrorEvent(e)
                        },
                        signInButtonModifier = Modifier
                            .wrapContentWidth()
                            .height(50.dp),
                        profileImageSize = 80.dp,
                        showLoginLabel = true
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (authState is SessionStatus.Authenticated) {
                        OptionsItem(
                            modifier = Modifier.padding(horizontal = 20.dp),
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
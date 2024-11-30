package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun LoginStatusComposable(
    sessionStatus: SessionStatus,
    noncePair: Pair<String, String>,
    onUserImageClick: () -> Unit,
    onSignInToSupabase: (String, String) -> Unit,
    onLoginError: (Exception) -> Unit,
    signInButtonModifier: Modifier,
    profileImageSize: Dp,
    showLoginLabel: Boolean
) {
    when (sessionStatus) {
        is SessionStatus.Authenticated -> {
            AsyncImage(
                model = sessionStatus.session.user?.userMetadata?.get(Constants.USER_AVATAR_URL_KEY)?.jsonPrimitive?.contentOrNull
                    ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(profileImageSize)
                    .clip(shape = CircleShape)
                    .clickable {
                        onUserImageClick()
                    },
                error = painterResource(R.drawable.ic_user)
            )
        }

        SessionStatus.Initializing -> {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = DialogOnBackground
            )
        }

        is SessionStatus.NotAuthenticated, is SessionStatus.RefreshFailure -> {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoogleSignInButton(
                    noncePair = noncePair,
                    signInToSupabase = { googleIdToken, rawNonce ->
                        onSignInToSupabase(googleIdToken, rawNonce)
                    },
                    errorLogin = { e ->
                        onLoginError(e)
                    },
                    signInButtonModifier = signInButtonModifier
                )
                if (showLoginLabel) {
                    Text(
                        stringResource(R.string.login_rationale),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
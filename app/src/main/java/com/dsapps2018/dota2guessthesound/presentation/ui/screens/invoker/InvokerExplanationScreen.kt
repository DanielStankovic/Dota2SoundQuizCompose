package com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile.AuthViewModel
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogBackground
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Exort
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Quas
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.Wex

@Composable
fun InvokerExplanationScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onPlayClicked: () -> Unit
) {

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
                        painterResource(id = R.drawable.invoker_bg),
                        contentScale = ContentScale.Crop
                    ),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    val offset = Offset(5.0f, 5.0f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            stringResource(R.string.invoker_game_mode_1),
                            color = Exort,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = DialogBackground,
                                    offset = offset,
                                    blurRadius = 3f
                                )
                            )
                        )
                        Text(
                            stringResource(R.string.invoker_game_mode_2),
                            color = Quas,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = DialogBackground,
                                    offset = offset,
                                    blurRadius = 3f
                                )
                            )
                        )
                        Text(
                            stringResource(R.string.invoker_game_mode_3),
                            color = Wex,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = DialogBackground,
                                    offset = offset,
                                    blurRadius = 3f
                                )
                            )
                        )
                    }
                    Spacer(Modifier.height(26.dp))
                    Text(
                        stringResource(R.string.explanation_2),
                        fontSize = 26.sp,
                        textAlign = TextAlign.Justify,
                        color = Color.White
                    )
                    Spacer(Modifier.height(40.dp))

                    MenuButton(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(200.dp),
                        paddingValues = PaddingValues(),
                        text = stringResource(R.string.play_lbl), textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        authViewModel.updateCoinValue(-50)
                        onPlayClicked()
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }

        }
    )
}
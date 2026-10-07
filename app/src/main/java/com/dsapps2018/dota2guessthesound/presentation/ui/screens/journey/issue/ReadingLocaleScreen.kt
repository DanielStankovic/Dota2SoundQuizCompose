package com.dsapps2018.dota2guessthesound.presentation.ui.screens.journey.issue

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.JourneyTimeAccent

@Composable
fun ReadingLocaleScreen(
    viewModel: ReadingLocaleViewModel = hiltViewModel(),
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nav by viewModel.nav.collectAsStateWithLifecycle()
    val fromOptions = viewModel.fromOptions

    BackHandler(enabled = !uiState.saving) {
        onBack()
    }

    LaunchedEffect(nav) {
        if (nav is ReadingLocaleViewModel.ReadingLocaleNav.Done) {
            viewModel.consumeNav()
            onDone()
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B1218), Color(0xFF15202B), Color(0xFF0B1218))
                    )
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(
                        if (fromOptions) R.string.reading_locale_options_title
                        else R.string.reading_locale_gate_title
                    ),
                    color = JourneyTimeAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        if (fromOptions) R.string.reading_locale_options_subtitle
                        else R.string.reading_locale_gate_subtitle
                    ),
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                )
                Spacer(Modifier.height(20.dp))

                ReadingLocalePicker(
                    locales = uiState.locales,
                    selectedCode = uiState.selectedCode,
                    onSelect = viewModel::select,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                )

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = Color(0xFFFF8A80),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                    )
                }

                Button(
                    onClick = viewModel::confirm,
                    enabled = !uiState.saving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JourneyTimeAccent,
                        contentColor = Color(0xFF0B1218),
                        disabledContainerColor = JourneyTimeAccent.copy(alpha = 0.45f),
                        disabledContentColor = Color(0xFF0B1218).copy(alpha = 0.6f),
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.reading_locale_confirm),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (uiState.saving) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = JourneyTimeAccent)
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.reading_locale_syncing),
                            color = Color.White,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}

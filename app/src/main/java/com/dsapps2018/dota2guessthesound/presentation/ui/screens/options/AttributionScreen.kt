package com.dsapps2018.dota2guessthesound.presentation.ui.screens.options

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.HyperlinkText
import com.dsapps2018.dota2guessthesound.presentation.ui.theme.DialogOnBackground

@Composable
fun AttributionScreen(
    modifier: Modifier = Modifier
) {
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
                        .padding(top = 70.dp, bottom = 8.dp)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HyperlinkText(
                        text = stringResource(R.string.attr_wallpapers),
                        linkText = listOf("Wallpapers.com"),
                        textAlign = TextAlign.Justify,
                        linkTextColor = DialogOnBackground,
                        hyperlinks = listOf("https://wallpapers.com/dota-2-phone")
                    )
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 2.dp, color = DialogOnBackground)
                    Spacer(Modifier.height(20.dp))
                    HyperlinkText(
                        text = stringResource(R.string.attr_freepik),
                        linkText = listOf("Freepik.com"),
                        textAlign = TextAlign.Justify,
                        linkTextColor = DialogOnBackground,
                        hyperlinks = listOf("https://www.freepik.com/")
                    )
                }
            }
        }
    )
}
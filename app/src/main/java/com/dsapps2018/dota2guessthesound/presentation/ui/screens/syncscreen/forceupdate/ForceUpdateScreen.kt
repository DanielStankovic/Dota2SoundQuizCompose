package com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen.forceupdate

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.MenuButton

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForceUpdateScreen() {
    val context = LocalContext.current

    BackHandler (enabled = true) {
        //Do nothing here
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = {padding ->
            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.home_background),
                        contentScale = ContentScale.Crop
                    ),

                ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 36.dp)
                ) {
                    Text("New Version Available", textAlign = TextAlign.Center, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("There are new features available, please update your app to continue using it", textAlign = TextAlign.Center, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MenuButton(
                        modifier = Modifier.height(100.dp).width(200.dp),
                        paddingValues = PaddingValues(horizontal = 40.dp, vertical = 10.dp),
                        text = "Update", textColor = Color.White,
                        contentScale = ContentScale.Fit
                    ) {
                        goToAppStore(context)
                    }
                }
            }

        }
    )
}

private fun goToAppStore(context: Context) {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
            )
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            )
        )

    }
}
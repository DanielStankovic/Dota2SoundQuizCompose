package com.dsapps2018.dota2guessthesound.presentation.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dsapps2018.dota2guessthesound.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerView() {
    val context = LocalContext.current
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp

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
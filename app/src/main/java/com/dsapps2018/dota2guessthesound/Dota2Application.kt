package com.dsapps2018.dota2guessthesound

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Arrays

@HiltAndroidApp
class Dota2Application : Application() {
    override fun onCreate() {
        super.onCreate()
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@Dota2Application) {
                if(BuildConfig.FLAVOR == "staging"){
                    val configuration = RequestConfiguration.Builder().setTestDeviceIds(listOf("8C69359E73B9951406C20FE64D0261A5")).build()
                    MobileAds.setRequestConfiguration(configuration)
                }
            }
        }
    }
}
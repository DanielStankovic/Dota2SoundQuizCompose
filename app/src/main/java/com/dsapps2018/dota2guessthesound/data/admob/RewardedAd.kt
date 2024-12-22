package com.dsapps2018.dota2guessthesound.data.admob

import android.content.Context
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.findActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

var isAdLoading = false
private val scope = CoroutineScope(Dispatchers.Default)
private val _rewardedAd = MutableStateFlow<RewardedAd?>(null)
val isAdReady: StateFlow<Boolean> =
    _rewardedAd.map { it != null }.stateIn(scope, SharingStarted.Lazily, initialValue = false)


fun loadRewardedAd(context: Context) {
    isAdLoading = true
    RewardedAd.load(
        context,
        context.getString(R.string.rewarded_video_ad_id),
        AdRequest.Builder().build(),
        object :
            RewardedAdLoadCallback() {
            override fun onAdLoaded(rewardedAd: RewardedAd) {
                _rewardedAd.value = rewardedAd
                isAdLoading = false
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                _rewardedAd.value = null
                isAdLoading = false
            }
        })
}

fun showRewardedAd(context: Context, onRewarded: () -> Unit, onAdDismissed: () -> Unit) {
    val activity = context.findActivity()
    if (_rewardedAd.value != null && activity != null) {
        _rewardedAd.value?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                _rewardedAd.value = null
                loadRewardedAd(context)
                onAdDismissed()
            }

            override fun onAdDismissedFullScreenContent() {
                if (!isAdLoading) {
                    _rewardedAd.value = null
                    loadRewardedAd(context)
                    onAdDismissed()
                }
            }
        }


        _rewardedAd.value?.show(activity) {
            _rewardedAd.value = null
            loadRewardedAd(context)
            onRewarded()
        }
    }
}

fun removeRewarded() {
    _rewardedAd.value?.fullScreenContentCallback = null
    _rewardedAd.value = null
}
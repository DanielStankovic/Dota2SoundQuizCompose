package com.dsapps2018.dota2guessthesound.presentation.ui.screens.options

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.OptionsItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    onPrivacyPolicyClick: () -> Unit,
    onChangelogClick: () -> Unit
) {
    val context = LocalContext.current
    val window = (LocalView.current.context as Activity).window
    val currentScreenWidth = LocalConfiguration.current.screenWidthDp
    DisposableEffect(Unit) {
        window.navigationBarColor = Color.Black.toArgb()
        onDispose {
            window.navigationBarColor = Color.Transparent.toArgb()
        }

    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OptionsItem(
                        leadingIcon = R.drawable.ic_notification,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.notifications)
                    ) {
                        openAppSettings(context, launcher)
                    }

                    OptionsItem(
                        leadingIcon = R.drawable.ic_privacy,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.privacy_policy)
                    ) {
                        onPrivacyPolicyClick()
                    }

                    OptionsItem(
                        leadingIcon = R.drawable.ic_log,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.changelog)
                    ) {
                        onChangelogClick()
                    }

                    OptionsItem(
                        leadingIcon = R.drawable.ic_report,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.report_problem)
                    ) {
                        sendEmail(context)
                    }

                    OptionsItem(
                        leadingIcon = R.drawable.ic_rating,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.rate_app)
                    ) {
                        launchInAppReview(context)
                    }

                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    factory = { context ->
                        AdView(context).apply {
                            setAdSize(
                                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                    context,
                                    currentScreenWidth
                                )
                            )
                            adUnitId = context.getString(R.string.banner_id)
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
            }
        }
    )
}

private fun openAppSettings(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val packageName = context.packageName
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    launcher.launch(intent)
}

private fun sendEmail(context: Context) {
    try {
        val selectorIntent = Intent(ACTION_SENDTO)
            .setData(context.getString(R.string.mail_to_email).toUri())
        val emailIntent = Intent(ACTION_SEND).apply {
            putExtra(EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
            putExtra(EXTRA_SUBJECT, context.getString(R.string.problem_lbl))
            selector = selectorIntent
        }

        context.startActivity(
            Intent.createChooser(
                emailIntent,
                context.getString(R.string.choose_an_email_client)
            )
        )

    } catch (e: ActivityNotFoundException) {
        Firebase.crashlytics.recordException(e)
    } catch (t: Throwable) {
        Firebase.crashlytics.recordException(t)
    }
}

private fun launchInAppReview(context: Context) {
    val reviewManager = ReviewManagerFactory.create(context as Activity)
    val requestReviewFlow = reviewManager.requestReviewFlow()
    requestReviewFlow.addOnCompleteListener { request ->
        if (request.isSuccessful) {
            val reviewInfo = request.result
            reviewManager.launchReviewFlow(context, reviewInfo)
        } else {
            request.exception?.cause?.let {
                Firebase.crashlytics.recordException(it)
            }
        }
    }
}
package com.dsapps2018.dota2guessthesound.presentation.ui.screens.options

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.AutoSizeText
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.OptionsItem
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onChangelogClick: () -> Unit,
    onAttributionsClicked: () -> Unit
) {
    val context = LocalContext.current

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
                    Spacer(Modifier.weight(1f))

                    OptionsItem(
                        leadingIcon = R.drawable.ic_user,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.profile)
                    ) {
                        onProfileClick()
                    }

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
                        leadingIcon = R.drawable.ic_privacy,
                        trailingIcon = R.drawable.ic_arrow_right,
                        optionText = stringResource(R.string.attributions)
                    ) {
                        onAttributionsClicked()
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
                        openAppOnGooglePlay(context, launcher)
                    }

                    Spacer(Modifier.weight(0.8f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AutoSizeText(
                            stringResource(
                                R.string.game_version,
                                BuildConfig.VERSION_NAME
                            ),
                            minTextSize = 12.sp,
                            maxTextSize = 18.sp,
                            maxLines = 1,
                            color = Color.White
                        )
                        AutoSizeText(
                            stringResource(R.string.dota_version, Constants.DOTA_VERSION),
                            minTextSize = 12.sp,
                            maxTextSize = 18.sp,
                            maxLines = 1,
                            color = Color.White
                        )
                    }
                }
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

private fun openAppOnGooglePlay(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val appPackageName = context.packageName
    val playStoreUri = Uri.parse("market://details?id=$appPackageName")
    val browserUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

    val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)

    // Check if the Play Store app is available
    if (playStoreIntent.resolveActivity(context.packageManager) != null) {
        launcher.launch(playStoreIntent)
    } else {
        // Fallback to the browser
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
        launcher.launch(browserIntent)
    }
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
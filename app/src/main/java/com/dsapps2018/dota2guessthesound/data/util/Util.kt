package com.dsapps2018.dota2guessthesound.data.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import com.dsapps2018.dota2guessthesound.BuildConfig
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.pow
import kotlin.math.round

fun getInitialModifiedDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSXXX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val calendar = Calendar.getInstance();
    calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
    return dateFormat.format(calendar.time)
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSXXX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val calendar = Calendar.getInstance();
    return dateFormat.format(calendar.time)
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun formatTimestampToLocalDateTime(timestamp: Long): String {
    // Convert the timestamp to a Date object
    val date = Date(timestamp)

    // Use the device's default locale
    val defaultLocale = Locale.getDefault()

    // Get the localized date and time format for the device's locale
    val dateTimeFormat =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, defaultLocale)

    // Format the date to a locale-specific string
    return dateTimeFormat.format(date)
}

fun getMonthFromStringDate(date: String): Int {
    return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).monthValue
}

fun getMonthStringFromStringDate(date: String): String {
    return ZonedDateTime.parse(
        date,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME
    ).month.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun getMonthYearStringFromStringDate(date: String): String {
    val parsedDate = ZonedDateTime.parse(
        date,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME
    )
    return "${
        parsedDate.month.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }  -  ${parsedDate.year}"
}

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

fun openDiscordInviteLink(context: Context, inviteLink: String) {
    // Create an Intent for the Discord invite link
    val discordIntent = Intent(Intent.ACTION_VIEW, Uri.parse(inviteLink))

    // Set the Discord app as the target if it's installed
    discordIntent.setPackage(Constants.DISCORD_PACKAGE_NAME)

    // Check if the Discord app can handle this Intent
    val canHandleDiscordIntent = discordIntent.resolveActivity(context.packageManager) != null

    // Use Discord app if available, otherwise fallback to browser
    if (!canHandleDiscordIntent) {
        discordIntent.setPackage(null) // Clear the package to let the system choose a browser
    }

    context.startActivity(discordIntent)
}

fun createSupabaseImgPath(bucketId: String, imageFileName: String): String {
    return "${BuildConfig.BASE_URL}/storage/v1/object/public/$bucketId/$imageFileName"
}


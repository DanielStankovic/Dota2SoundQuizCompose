package com.dsapps2018.dota2guessthesound.data.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
    ).month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

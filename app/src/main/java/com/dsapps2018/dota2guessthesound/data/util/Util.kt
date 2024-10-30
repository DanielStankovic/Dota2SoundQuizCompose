package com.dsapps2018.dota2guessthesound.data.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun getInitialModifiedDate(): String {
     val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSXXX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val calendar = Calendar.getInstance();
    calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
    return dateFormat.format(calendar.time)
}
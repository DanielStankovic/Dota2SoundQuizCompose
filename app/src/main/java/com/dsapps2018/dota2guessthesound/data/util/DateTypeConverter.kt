package com.dsapps2018.dota2guessthesound.data.util

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateTypeConverter {

    // Define the date format based on your input string
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSXXX", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    @TypeConverter
    fun fromStringToTimestamp(dateString: String?): Long? {
        return dateString?.let {
            try {
                val date = dateFormat.parse(it)
                date?.time // Convert to timestamp (milliseconds)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    @TypeConverter
    fun fromTimestampToString(timestamp: Long?): String? {
        return timestamp?.let {
            dateFormat.format(Date(it)) // Convert back to string
        }
    }
}
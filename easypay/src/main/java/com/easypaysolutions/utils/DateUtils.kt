package com.easypaysolutions.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

internal object DateUtils {

    private const val OUTPUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    @SuppressLint("SimpleDateFormat")
    fun convertToUtcFromServer(timestampWithOffset: String): String? {
        val dateString = extractEpoch(timestampWithOffset)
        val components = dateString.split("-")

        if (components.count() != 2) {
            return null
        }

        val timestamp = components[0].toLongOrNull() ?: return null
        val offset = components[1].toIntOrNull() ?: return null
        val utcTimestamp = timestamp - (offset * 3600)
        val date = Date(utcTimestamp)

        val dateFormatter = SimpleDateFormat(OUTPUT_DATE_FORMAT)
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        return dateFormatter.format(date)
    }

    private fun extractEpoch(serverDate: String): String {
        return serverDate
            .replace("/Date(", "")
            .replace(")/", "")
    }

    fun parseDateForApi(startDate: Date): String {
        return """\/Date(${startDate.time})\/"""
    }
}
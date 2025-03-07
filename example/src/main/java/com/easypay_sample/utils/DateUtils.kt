package com.easypay_sample.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {

    private const val USER_DATE_FORMAT = "MM-dd-yyyy"

    @SuppressLint("SimpleDateFormat")
    fun parseDate(date: String): Date? {
        return try {
            val format = SimpleDateFormat(USER_DATE_FORMAT)
            format.parse(date)
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDate(date: Date): String? {
        val format = SimpleDateFormat(USER_DATE_FORMAT)
        return format.format(date)
    }
}
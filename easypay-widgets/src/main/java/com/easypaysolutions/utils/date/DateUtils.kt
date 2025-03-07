package com.easypaysolutions.utils.date

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import kotlin.jvm.Throws

internal object DateUtils {
    private const val EXPIRATION_DATE_FORMAT = "MM/yy"

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseExpirationDate(expirationDate: String): Date? {
        val dateFormatter = SimpleDateFormat(EXPIRATION_DATE_FORMAT)
        dateFormatter.isLenient = false
        return dateFormatter.parse(expirationDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getLatestInvalidExpDate(): Date? {
        return try {
            val dateFormatter = SimpleDateFormat(EXPIRATION_DATE_FORMAT)
            dateFormatter.isLenient = false

            val gc = GregorianCalendar()
            gc.add(Calendar.MONTH, -1)
            dateFormatter.parse(dateFormatter.format(gc.time))
        } catch (exception: Exception) {
            null
        }
    }

    /**
     * Pass expiration date in MM/yy format
     */
    fun getMonthFromExpirationDate(expirationDate: String): String {
        return expirationDate.substring(0, 2)
    }

    /**
     * Pass expiration date in MM/yy format
     */
    fun getYearFromExpirationDate(expirationDate: String): String {
        return expirationDate.substring(3)
    }
}
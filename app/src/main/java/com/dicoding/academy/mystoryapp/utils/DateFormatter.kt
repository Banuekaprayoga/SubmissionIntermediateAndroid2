package com.dicoding.academy.mystoryapp.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    fun formatDate(currentDate: String?): String? {
        val currentFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val targetFormat = "dd MMM yyyy"
        val currentDf = SimpleDateFormat(currentFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val targetDf = SimpleDateFormat(targetFormat, Locale.ENGLISH)
        return try {
            currentDate?.let {
                val date = currentDf.parse(it)
                targetDf.format(date!!)
            }
        } catch (ex: ParseException) {
            ex.printStackTrace()
            null
        }
    }
}
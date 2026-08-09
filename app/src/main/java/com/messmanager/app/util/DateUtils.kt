package com.messmanager.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)

    fun todayIso(): String {
        return LocalDate.now().format(isoFormatter)
    }

    fun formatDisplay(isoDate: String): String {
        return try {
            val date = LocalDate.parse(isoDate, isoFormatter)
            date.format(displayFormatter)
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatMonthYear(month: Int, year: Int): String {
        val date = LocalDate.of(year, month, 1)
        return date.format(monthYearFormatter)
    }
}

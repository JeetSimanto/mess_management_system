package com.messmanager.app.util

import java.text.DecimalFormat

object CurrencyFormatter {
    private val bdtFormat = DecimalFormat("৳ #,##0.00")
    private val bdtIntegerFormat = DecimalFormat("৳ #,##0")
    private val rateFormat = DecimalFormat("৳ #,##0.00")

    fun formatBdt(amountBdt: Double): String {
        return bdtFormat.format(amountBdt)
    }

    fun formatPaisa(amountPaisa: Long): String {
        return bdtFormat.format(amountPaisa / 100.0)
    }

    fun formatIntegerBdt(amountBdt: Double): String {
        return bdtIntegerFormat.format(amountBdt)
    }

    fun formatMealRate(rateBdt: Double): String {
        return "${rateFormat.format(rateBdt)} / meal"
    }

    fun bdtToPaisa(amountBdt: Double): Long {
        return (amountBdt * 100).toLong()
    }
}

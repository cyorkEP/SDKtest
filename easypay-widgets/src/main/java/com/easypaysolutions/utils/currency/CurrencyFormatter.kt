package com.easypaysolutions.utils.currency

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal object CurrencyFormatter {

    fun formatCurrency(amount: Double, locale: Locale = Locale.US): String {
        val format = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(2)
        format.currency = Currency.getInstance(locale)
        return format.format(amount)
    }
}
package com.easypaysolutions.payment_sheet.utils

import com.easypaysolutions.payment_sheet.PaymentSheet

internal interface PaymentSheetLauncher {
    fun present(configuration: PaymentSheet.Configuration? = null)
}
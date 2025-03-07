package com.easypaysolutions.utils

import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult

internal fun assertCompleted(result: PaymentSheetResult) {
    assert(result is PaymentSheetResult.Completed)
}

internal fun assertFailed(result: PaymentSheetResult) {
    assert(result is PaymentSheetResult.Failed)
}

@Suppress("UNUSED_PARAMETER")
internal fun expectNoResult(result: PaymentSheetResult) {
    error("Shouldn't call PaymentSheetResultCallback")
}

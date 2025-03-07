package com.easypaysolutions.utils

import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult

internal fun assertSelected(result: CustomerSheetResult) {
    assert(result is CustomerSheetResult.Selected)
    assert((result as? CustomerSheetResult.Selected)?.annualConsentId != null)
}

internal fun assertFailed(result: CustomerSheetResult) {
    assert(result is CustomerSheetResult.Failed)
}

@Suppress("UNUSED_PARAMETER")
internal fun expectNoResult(result: CustomerSheetResult) {
    error("Shouldn't call CustomerSheetResultCallback")
}
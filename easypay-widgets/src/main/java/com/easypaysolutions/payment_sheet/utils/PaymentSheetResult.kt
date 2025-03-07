package com.easypaysolutions.payment_sheet.utils

import android.os.Parcelable
import com.easypaysolutions.common.utils.ConsentExcerpt
import kotlinx.parcelize.Parcelize

sealed interface PaymentSheetResult: Parcelable {

    @Parcelize
    data class Completed(val data: PaymentSheetCompletedData,
                         val addedConsents: List<ConsentExcerpt>?,
                         val deletedConsents: List<Int>?) : PaymentSheetResult

    @Parcelize
    data object Canceled : PaymentSheetResult

    @Parcelize
    data class Failed(val error: Throwable) : PaymentSheetResult
}

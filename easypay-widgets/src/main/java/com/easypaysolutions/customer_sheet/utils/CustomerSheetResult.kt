package com.easypaysolutions.customer_sheet.utils

import android.os.Parcelable
import com.easypaysolutions.common.utils.ConsentExcerpt
import kotlinx.parcelize.Parcelize

sealed interface CustomerSheetResult : Parcelable {

    @Parcelize
    data class Selected(val selectedConsentId: Int?,
                        val addedConsents: List<ConsentExcerpt>,
                        val deletedConsents: List<Int>) : CustomerSheetResult

    @Parcelize
    data class Failed(
        val error: Throwable,
    ) : CustomerSheetResult
}

package com.easypaysolutions.payment_sheet.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentSheetCompletedData(
    val functionOk: Boolean,
    val errorCode: Int,
    val errorMessage: String,
    val responseMessage: String,
    val txApproved: Boolean,
    val txId: Int,
    val txCode: String,
    val avsResult: String,
    val acquirerResponseEmv: String?,
    val cvvResult: String,
    val isPartialApproval: Boolean,
    val requiresVoiceAuth: Boolean,
    val responseApprovedAmount: Double,
    val responseAuthorizedAmount: Double,
    val responseBalanceAmount: Double,
): Parcelable

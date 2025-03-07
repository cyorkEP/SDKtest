package com.easypaysolutions.payment_sheet.utils

import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult

internal fun ChargeCreditCardResult.mapToPaymentSheetCompletedData(): PaymentSheetCompletedData {
    return PaymentSheetCompletedData(
        functionOk = functionOk,
        errorCode = errorCode,
        errorMessage = errorMessage,
        responseMessage = responseMessage,
        txApproved = txApproved,
        txId = txId,
        txCode = txCode,
        avsResult = avsResult,
        acquirerResponseEmv = acquirerResponseEmv,
        cvvResult = cvvResult,
        isPartialApproval = isPartialApproval,
        requiresVoiceAuth = requiresVoiceAuth,
        responseApprovedAmount = responseApprovedAmount,
        responseAuthorizedAmount = responseAuthorizedAmount,
        responseBalanceAmount = responseBalanceAmount
    )
}

internal fun ProcessPaymentAnnualResult.mapToPaymentSheetCompletedData(): PaymentSheetCompletedData {
    return PaymentSheetCompletedData(
        functionOk = functionOk,
        errorCode = errorCode,
        errorMessage = errorMessage,
        responseMessage = responseMessage,
        txApproved = txApproved,
        txId = txId,
        txCode = txCode,
        avsResult = avsResult,
        acquirerResponseEmv = acquirerResponseEmv,
        cvvResult = cvvResult,
        isPartialApproval = isPartialApproval,
        requiresVoiceAuth = requiresVoiceAuth,
        responseApprovedAmount = responseApprovedAmount,
        responseAuthorizedAmount = responseAuthorizedAmount,
        responseBalanceAmount = responseBalanceAmount
    )
}
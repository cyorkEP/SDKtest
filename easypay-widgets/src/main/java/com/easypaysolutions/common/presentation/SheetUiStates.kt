package com.easypaysolutions.common.presentation

import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.common.exceptions.EasyPayWidgetException
import com.easypaysolutions.payment_sheet.utils.PaymentSheetCompletedData

internal sealed class PayWithSavedCardUiState {
    data class Success(val result: PaymentSheetCompletedData) : PayWithSavedCardUiState()
    data class Error(val exception: EasyPayWidgetException) : PayWithSavedCardUiState()
    data class Declined(val result: ProcessPaymentAnnualResult) : PayWithSavedCardUiState()
    data object Loading : PayWithSavedCardUiState()
}

internal sealed class PayWithNewCardUiState {
    data class Success(val result: PaymentSheetCompletedData) : PayWithNewCardUiState()
    data class Error(val exception: EasyPayWidgetException) : PayWithNewCardUiState()
    data class Declined(val result: ChargeCreditCardResult) : PayWithNewCardUiState()
    data object Loading : PayWithNewCardUiState()
}

internal sealed class AddNewCardUiState {
    data class Success(val result: CreateAnnualConsentResult) : AddNewCardUiState()
    data class Error(val exception: EasyPayWidgetException) : AddNewCardUiState()
    data object Loading : AddNewCardUiState()
}

internal sealed class PaymentMethodsUiState {
    data class Success(val methods: List<AnnualConsent>) : PaymentMethodsUiState()
    data class Error(val exception: EasyPayWidgetException) : PaymentMethodsUiState()
    data object Loading : PaymentMethodsUiState()
}

internal sealed class DeleteCardUiState {
    data class Success(val result: CancelAnnualConsentResult) : DeleteCardUiState()
    data class Error(val exception: EasyPayWidgetException) : DeleteCardUiState()
    data object Loading : DeleteCardUiState()
}

internal sealed class OpenNewCardSheetUiState {
    data object Idle : OpenNewCardSheetUiState()
    data object Open : OpenNewCardSheetUiState()
    data object Close : OpenNewCardSheetUiState()
}
package com.easypay_sample.ui.consent_annual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsentBodyParams
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsents
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsentsBodyParams
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnual
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnualParams
import com.easypay_sample.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias ConsentAnnualState = ViewState<ListAnnualConsentsResult>
typealias ProcessPaymentAnnualState = ViewState<ProcessPaymentAnnualResult>
typealias DeleteAnnualConsentState = ViewState<CancelAnnualConsentResult>

@HiltViewModel
class ConsentAnnualViewModel @Inject constructor(
    private val listAnnualConsents: ListAnnualConsents,
    private val processPaymentAnnual: ProcessPaymentAnnual,
    private val cancelAnnualConsent: CancelAnnualConsent,
) : ViewModel() {

    private val _annualConsents = MutableStateFlow<ConsentAnnualState>(ViewState.Loading())
    val annualConsents: StateFlow<ConsentAnnualState> = _annualConsents

    private val _processPaymentAnnualResult = MutableSharedFlow<ProcessPaymentAnnualState>()
    val processPaymentAnnualResult: SharedFlow<ProcessPaymentAnnualState> =
        _processPaymentAnnualResult

    private val _deleteAnnualConsentResult = MutableSharedFlow<DeleteAnnualConsentState>()
    val deleteAnnualConsentResult: SharedFlow<DeleteAnnualConsentState> =
        _deleteAnnualConsentResult

    //region Public

    fun fetchAnnualConsents() {
        viewModelScope.launch {
            val params = prepareParams()
            val response = listAnnualConsents.listAnnualConsents(params)
            when (response.status) {
                NetworkResource.Status.SUCCESS -> {
                    response.data?.let {
                        _annualConsents.emit(ViewState.Success(data = it))
                    } ?: _annualConsents.emit(ViewState.Error())
                }

                NetworkResource.Status.ERROR -> _annualConsents.emit(ViewState.Error(message = response.error?.message))
                else -> {}
            }
        }
    }

    fun deleteConsent(consent: AnnualConsent) {
        viewModelScope.launch {
            _deleteAnnualConsentResult.emit(ViewState.Loading())
            val params = CancelAnnualConsentBodyParams(consentId = consent.id)
            val response = cancelAnnualConsent.cancelAnnualConsent(params)
            when (response.status) {
                NetworkResource.Status.SUCCESS -> {
                    response.data?.let {
                        _deleteAnnualConsentResult.emit(ViewState.Success(data = it))
                        fetchAnnualConsents()
                    } ?: _deleteAnnualConsentResult.emit(ViewState.Error())
                }

                NetworkResource.Status.ERROR -> emitErrorForDeleteConsent("ERROR: " + response.error?.message)
                NetworkResource.Status.DECLINED -> emitErrorForDeleteConsent("DECLINED: " + response.error?.message)
            }
        }
    }

    fun chargeConsent(consent: AnnualConsent, amount: Double) {
        viewModelScope.launch {
            _processPaymentAnnualResult.emit(ViewState.Loading())
            val params = ProcessPaymentAnnualParams(consentId = consent.id, processAmount = amount)
            val response = processPaymentAnnual.processPaymentAnnual(params)
            when (response.status) {
                NetworkResource.Status.SUCCESS -> {
                    response.data?.let {
                        _processPaymentAnnualResult.emit(ViewState.Success(data = it))
                    } ?: _processPaymentAnnualResult.emit(ViewState.Error())
                }

                NetworkResource.Status.ERROR -> emitErrorForProcessPayment("ERROR: " + response.error?.message)
                NetworkResource.Status.DECLINED -> emitErrorForProcessPayment("DECLINED: " + response.error?.message)
            }
        }
    }

    //endregion

    //region Private

    private fun prepareParams(): ListAnnualConsentsBodyParams = ListAnnualConsentsBodyParams(
        merchantId = 1,
        customerReferenceId = "123456"
    )

    private suspend fun emitErrorForDeleteConsent(message: String?) {
        _deleteAnnualConsentResult.emit(ViewState.Error(message))
    }

    private suspend fun emitErrorForProcessPayment(message: String?) {
        _processPaymentAnnualResult.emit(ViewState.Error(message))
    }

    //endregion

}
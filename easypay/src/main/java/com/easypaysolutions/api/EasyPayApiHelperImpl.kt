package com.easypaysolutions.api

import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.CreateAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.ListAnnualConsentsRequest
import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualRequest
import com.easypaysolutions.api.requests.base.ApiRequest
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardRequest
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.NetworkDataSource
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.networking.authentication.AuthHelper

internal class EasyPayApiHelperImpl(
    private val easyPayService: EasyPayService,
    private val networkDataSource: NetworkDataSource,
    private val authHelper: AuthHelper,
) : EasyPayApiHelper {

    //region Overridden

    override suspend fun listAnnualConsents(
        request: ListAnnualConsentsRequest,
    ): NetworkResource<ListAnnualConsentsResult> = networkDataSource.getResult {
        val header = getHeader(request)
        easyPayService.listAnnualConsents(header, request.body)
    }

    override suspend fun chargeCreditCard(
        request: ChargeCreditCardRequest,
    ): NetworkResource<ChargeCreditCardResult> = networkDataSource.getTransactionResult {
        val header = getHeader(request)
        easyPayService.cardSaleManual(header, request.body)
    }

    override suspend fun createAnnualConsent(
        request: CreateAnnualConsentRequest,
    ): NetworkResource<CreateAnnualConsentResult> = networkDataSource.getResult {
        val header = getHeader(request)
        easyPayService.createAnnualConsent(header, request.body)
    }

    override suspend fun cancelAnnualConsent(
        request: CancelAnnualConsentRequest,
    ): NetworkResource<CancelAnnualConsentResult> = networkDataSource.getResult {
        val header = getHeader(request)
        easyPayService.cancelAnnualConsent(header, request.body)
    }

    override suspend fun processPaymentAnnual(
        request: ProcessPaymentAnnualRequest,
    ): NetworkResource<ProcessPaymentAnnualResult> = networkDataSource.getTransactionResult {
        val header = getHeader(request)
        easyPayService.processPaymentAnnual(header, request.body)
    }

    //endregion

    //region Private

    private fun getHeader(request: ApiRequest<*>): String {
        return authHelper.getSessKey(request.userDataPresent)
    }

    //endregion

}
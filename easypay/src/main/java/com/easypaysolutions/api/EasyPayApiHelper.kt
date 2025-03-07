package com.easypaysolutions.api

import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.CreateAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.ListAnnualConsentsRequest
import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualRequest
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardRequest
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.NetworkResource

internal interface EasyPayApiHelper {

    suspend fun listAnnualConsents(request: ListAnnualConsentsRequest): NetworkResource<ListAnnualConsentsResult>

    suspend fun chargeCreditCard(request: ChargeCreditCardRequest): NetworkResource<ChargeCreditCardResult>

    suspend fun createAnnualConsent(request: CreateAnnualConsentRequest): NetworkResource<CreateAnnualConsentResult>

    suspend fun cancelAnnualConsent(request: CancelAnnualConsentRequest): NetworkResource<CancelAnnualConsentResult>

    suspend fun processPaymentAnnual(request: ProcessPaymentAnnualRequest): NetworkResource<ProcessPaymentAnnualResult>
}
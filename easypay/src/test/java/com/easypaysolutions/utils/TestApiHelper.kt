package com.easypaysolutions.utils

import com.easypaysolutions.api.EasyPayApiHelper
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
import org.mockito.Mockito

internal class TestApiHelper : EasyPayApiHelper {
    override suspend fun listAnnualConsents(request: ListAnnualConsentsRequest): NetworkResource<ListAnnualConsentsResult> {
        return NetworkResource.success(Mockito.mock())
    }

    override suspend fun chargeCreditCard(request: ChargeCreditCardRequest): NetworkResource<ChargeCreditCardResult> {
        return NetworkResource.success(Mockito.mock())
    }

    override suspend fun createAnnualConsent(request: CreateAnnualConsentRequest): NetworkResource<CreateAnnualConsentResult> {
        return NetworkResource.success(Mockito.mock())
    }

    override suspend fun cancelAnnualConsent(request: CancelAnnualConsentRequest): NetworkResource<CancelAnnualConsentResult> {
        return NetworkResource.success(Mockito.mock())
    }

    override suspend fun processPaymentAnnual(request: ProcessPaymentAnnualRequest): NetworkResource<ProcessPaymentAnnualResult> {
        return NetworkResource.success(Mockito.mock())
    }
}
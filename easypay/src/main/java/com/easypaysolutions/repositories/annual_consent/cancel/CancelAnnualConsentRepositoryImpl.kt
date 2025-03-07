package com.easypaysolutions.repositories.annual_consent.cancel

import com.easypaysolutions.api.EasyPayApiHelper
import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentRequest
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.networking.NetworkResource

internal class CancelAnnualConsentRepositoryImpl(
    private val apiHelper: EasyPayApiHelper,
) : CancelAnnualConsentRepository {

    override suspend fun cancelAnnualConsent(
        params: CancelAnnualConsentBodyParams,
    ): NetworkResource<CancelAnnualConsentResult> {
        val request = CancelAnnualConsentRequest(
            userDataPresent = true,
            body = params.toDto(),
        )
        return apiHelper.cancelAnnualConsent(request)
    }
}
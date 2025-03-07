package com.easypaysolutions.repositories.annual_consent.cancel

import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.networking.NetworkResource

internal interface CancelAnnualConsentRepository {
    suspend fun cancelAnnualConsent(
        params: CancelAnnualConsentBodyParams,
    ): NetworkResource<CancelAnnualConsentResult>
}
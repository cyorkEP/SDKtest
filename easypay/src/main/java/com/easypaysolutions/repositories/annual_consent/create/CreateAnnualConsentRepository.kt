package com.easypaysolutions.repositories.annual_consent.create

import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.networking.NetworkResource

internal interface CreateAnnualConsentRepository {
    suspend fun createAnnualConsent(
        params: CreateAnnualConsentBodyParams,
    ): NetworkResource<CreateAnnualConsentResult>
}
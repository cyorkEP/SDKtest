package com.easypaysolutions.repositories.annual_consent.create

import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.networking.NetworkResource
import org.koin.java.KoinJavaComponent

class CreateAnnualConsent {

    private val createAnnualConsentRepository: CreateAnnualConsentRepository by KoinJavaComponent.inject(
        CreateAnnualConsentRepository::class.java
    )

    suspend fun createAnnualConsent(
        params: CreateAnnualConsentBodyParams,
    ): NetworkResource<CreateAnnualConsentResult> {
        return createAnnualConsentRepository.createAnnualConsent(params)
    }
}
package com.easypaysolutions.repositories.annual_consent.list

import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.networking.NetworkResource
import org.koin.java.KoinJavaComponent.inject

class ListAnnualConsents {

    private val consentAnnualRepository: ListAnnualConsentsRepository by inject(
        ListAnnualConsentsRepository::class.java
    )

    suspend fun listAnnualConsents(params: ListAnnualConsentsBodyParams): NetworkResource<ListAnnualConsentsResult> {
        return consentAnnualRepository.listAnnualConsents(params)
    }
}
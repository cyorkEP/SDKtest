package com.easypaysolutions.repositories.annual_consent.list

import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.networking.NetworkResource

internal interface ListAnnualConsentsRepository {
    suspend fun listAnnualConsents(params: ListAnnualConsentsBodyParams): NetworkResource<ListAnnualConsentsResult>
}
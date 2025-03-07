package com.easypaysolutions.repositories.annual_consent.create

import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.Validator
import com.easypaysolutions.utils.validation.ValidationErrorMessages.EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID
import com.easypaysolutions.utils.validation.ValidatorUtils

internal class CreateAnnualConsentValidator : Validator<CreateAnnualConsentBodyParams> {
    override suspend fun <T : ApiResult> validate(
        params: CreateAnnualConsentBodyParams,
        call: suspend () -> NetworkResource<T>,
    ): NetworkResource<T> {
        val validationError =
            ValidatorUtils.validate(params) ?: ValidatorUtils.validateAtLeastOneNotBlank(
                listOf(
                    params.consentCreator.rpguid,
                    params.consentCreator.customerReferenceId
                ),
                EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID
            )

        validationError?.let {
            return NetworkResource.error(it)
        }

        return call()
    }
}
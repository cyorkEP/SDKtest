package com.easypaysolutions.repositories.annual_consent.list

import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.Validator
import com.easypaysolutions.utils.validation.ValidationErrorMessages.EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID
import com.easypaysolutions.utils.validation.ValidatorUtils

internal class ListAnnualConsentsValidator: Validator<ListAnnualConsentsBodyParams> {
    override suspend fun <T : ApiResult> validate(
        params: ListAnnualConsentsBodyParams,
        call: suspend () -> NetworkResource<T>,
    ): NetworkResource<T> {
        val validationError =
            ValidatorUtils.validate(params) ?: ValidatorUtils.validateAtLeastOneNotBlank(
                listOf(
                    params.rpguid,
                    params.customerReferenceId
                ),
                EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID
            )

        validationError?.let {
            return NetworkResource.error(it)
        }

        return call()    }
}
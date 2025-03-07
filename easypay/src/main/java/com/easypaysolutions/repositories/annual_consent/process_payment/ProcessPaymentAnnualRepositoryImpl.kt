package com.easypaysolutions.repositories.annual_consent.process_payment

import com.easypaysolutions.api.EasyPayApiHelper
import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualRequest
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.validation.ValidatorUtils

internal class ProcessPaymentAnnualRepositoryImpl(
    private val apiHelper: EasyPayApiHelper,
) : ProcessPaymentAnnualRepository {

    override suspend fun processPaymentAnnual(
        params: ProcessPaymentAnnualParams,
    ): NetworkResource<ProcessPaymentAnnualResult> = ValidatorUtils.validate(params) {
        val request = ProcessPaymentAnnualRequest(
            userDataPresent = true,
            body = params.toDto(),
        )
        apiHelper.processPaymentAnnual(request)
    }
}
package com.easypaysolutions.repositories.annual_consent.process_payment

import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.networking.NetworkResource

internal interface ProcessPaymentAnnualRepository {
    suspend fun processPaymentAnnual(params: ProcessPaymentAnnualParams): NetworkResource<ProcessPaymentAnnualResult>
}
package com.easypaysolutions.api.requests.annual_consent

import com.easypaysolutions.api.requests.base.ApiRequest
import com.google.gson.annotations.SerializedName

internal data class ProcessPaymentAnnualRequest(
    override val userDataPresent: Boolean,
    override val body: ProcessPaymentAnnualBodyDto,
) : ApiRequest<ProcessPaymentAnnualBodyDto>(userDataPresent, body)

internal data class ProcessPaymentAnnualBodyDto(
    @SerializedName("ConsentID")
    val consentId: Int,

    @SerializedName("ProcessAmount")
    val processAmount: String,
)
package com.easypaysolutions.api.requests.annual_consent

import com.easypaysolutions.api.requests.base.ApiRequest
import com.google.gson.annotations.SerializedName

internal data class ListAnnualConsentsRequest(
    override val userDataPresent: Boolean,
    override val body: ConsentAnnualQuery,
) : ApiRequest<ConsentAnnualQuery>(userDataPresent, body)

internal data class ConsentAnnualQuery(
    @SerializedName("Query")
    val query: String,
) {
    enum class Variable(val value: String) {
        MERCHANT_ID("A"),
        CUSTOMER_REF_ID("F"),
        CONSENT_TYPE("G"),
        ENABLED("H"),
        RPGUID("J")
    }
}

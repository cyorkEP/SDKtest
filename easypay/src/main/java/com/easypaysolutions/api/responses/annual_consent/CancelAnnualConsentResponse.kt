package com.easypaysolutions.api.responses.annual_consent

import com.easypaysolutions.api.responses.base.ApiResponse
import com.easypaysolutions.api.responses.base.ApiResult
import com.google.gson.annotations.SerializedName

internal data class CancelAnnualConsentResponse(
    @SerializedName("ConsentAnnual_CancelResult")
    override val result: CancelAnnualConsentResult,
) : ApiResponse<CancelAnnualConsentResult>(result)

data class CancelAnnualConsentResult internal constructor(
    @SerializedName("FunctionOk")
    override val functionOk: Boolean,

    @SerializedName("ErrCode")
    override val errorCode: Int,

    @SerializedName("ErrMsg")
    override val errorMessage: String,

    @SerializedName("RespMsg")
    override val responseMessage: String,

    @SerializedName("CancelSuccess")
    val cancelSuccess: Boolean,

    @SerializedName("CancelledConsentID")
    val cancelledConsentId: Int,
) : ApiResult(
    functionOk,
    errorCode,
    errorMessage,
    responseMessage
)
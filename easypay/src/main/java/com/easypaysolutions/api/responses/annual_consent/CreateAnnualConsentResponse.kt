package com.easypaysolutions.api.responses.annual_consent

import com.easypaysolutions.api.responses.base.ApiResponse
import com.easypaysolutions.api.responses.base.ApiResult
import com.google.gson.annotations.SerializedName

internal data class CreateAnnualConsentResponse(
    @SerializedName("ConsentAnnual_Create_MANResult")
    override val result: CreateAnnualConsentResult,
) : ApiResponse<CreateAnnualConsentResult>(result)

data class CreateAnnualConsentResult internal constructor(
    @SerializedName("FunctionOk")
    override val functionOk: Boolean,

    @SerializedName("ErrCode")
    override val errorCode: Int,

    @SerializedName("ErrMsg")
    override val errorMessage: String,

    @SerializedName("RespMsg")
    override val responseMessage: String,

    @SerializedName("ConsentID")
    val consentId: Int,

    @SerializedName("CreationSuccess")
    val creationSuccess: Boolean,

    @SerializedName("PreConsentAuthMessage")
    val preConsentAuthMessage: String,

    @SerializedName("PreConsentAuthSuccess")
    val preConsentAuthSuccess: Boolean,

    @SerializedName("PreConsentAuthTxID")
    val preConsentAuthTxId: Int,
) : ApiResult(
    functionOk,
    errorCode,
    errorMessage,
    responseMessage
)
package com.easypaysolutions.api.responses.annual_consent

import com.easypaysolutions.api.responses.base.ApiResponse
import com.easypaysolutions.api.responses.base.TransactionResult
import com.google.gson.annotations.SerializedName

internal data class ProcessPaymentAnnualResponse(
    @SerializedName("ConsentAnnual_ProcPaymentResult")
    override val result: ProcessPaymentAnnualResult,
) : ApiResponse<ProcessPaymentAnnualResult>(result)

data class ProcessPaymentAnnualResult internal constructor(
    @SerializedName("FunctionOk")
    override val functionOk: Boolean,

    @SerializedName("ErrCode")
    override val errorCode: Int,

    @SerializedName("ErrMsg")
    override val errorMessage: String,

    @SerializedName("RespMsg")
    override val responseMessage: String,

    @SerializedName("TxApproved")
    override val txApproved: Boolean,

    @SerializedName("TxID")
    override val txId: Int,

    @SerializedName("TxnCode")
    override val txCode: String,

    @SerializedName("AVSresult")
    val avsResult: String,

    @SerializedName("AcquirerResponseEMV")
    val acquirerResponseEmv: String?,

    @SerializedName("CVVresult")
    val cvvResult: String,

    @SerializedName("IsPartialApproval")
    val isPartialApproval: Boolean,

    @SerializedName("RequiresVoiceAuth")
    val requiresVoiceAuth: Boolean,

    @SerializedName("ResponseApprovedAmount")
    val responseApprovedAmount: Double,

    @SerializedName("ResponseAuthorizedAmount")
    val responseAuthorizedAmount: Double,

    @SerializedName("ResponseBalanceAmount")
    val responseBalanceAmount: Double,
) : TransactionResult(
    functionOk,
    errorCode,
    errorMessage,
    responseMessage,
    txApproved,
    txId,
    txCode
)
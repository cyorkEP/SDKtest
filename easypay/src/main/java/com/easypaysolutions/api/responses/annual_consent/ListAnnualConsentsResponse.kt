package com.easypaysolutions.api.responses.annual_consent

import com.easypaysolutions.api.responses.base.ApiResponse
import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.utils.DateUtils
import com.google.gson.annotations.SerializedName

internal data class ListAnnualConsentsResponse(
    @SerializedName("ConsentAnnual_QueryResult")
    override val result: ListAnnualConsentsResult,
) : ApiResponse<ListAnnualConsentsResult>(result)

data class ListAnnualConsentsResult internal constructor(
    @SerializedName("FunctionOk")
    override val functionOk: Boolean,

    @SerializedName("ErrCode")
    override val errorCode: Int,

    @SerializedName("ErrMsg")
    override val errorMessage: String,

    @SerializedName("RespMsg")
    override val responseMessage: String,

    @SerializedName("NumRecords")
    val numRecords: Int,

    @SerializedName("Consents")
    val consents: List<AnnualConsent>,
) : ApiResult(
    functionOk,
    errorCode,
    errorMessage,
    responseMessage
) {
    override fun parseIfNeeded() {
        consents.forEach { it.parseDates() }
    }
}

data class AnnualConsent internal constructor(
    @SerializedName("AcctHolderFirstName")
    val accountHolderFirstName: String,

    @SerializedName("AcctHolderID")
    val accountHolderId: Int,

    @SerializedName("AcctHolderLastName")
    val accountHolderLastName: String,

    @SerializedName("AcctNo")
    val accountNumber: String,

    @SerializedName("AuthTxID")
    val authTxId: Int,

    @SerializedName("CreatedBy")
    val createdBy: String,

    @SerializedName("CreatedOn")
    var createdOn: String,

    @SerializedName("CustID")
    val customerId: Int,

    @SerializedName("CustomerRefID")
    val customerReferenceId: String,

    @SerializedName("EndDate")
    var endDate: String,

    @SerializedName("ID")
    val id: Int,

    @SerializedName("IsEnabled")
    val isEnabled: Boolean,

    @SerializedName("LimitLifeTime")
    val limitLifeTime: Double,

    @SerializedName("LimitPerCharge")
    val limitPerCharge: Double,

    @SerializedName("MerchID")
    val merchId: Int,

    @SerializedName("NumDays")
    val numDays: Int,

    @SerializedName("RPGUID")
    val rpguid: String,

    @SerializedName("ServiceDescrip")
    val serviceDescription: String,

    @SerializedName("StartDate")
    var startDate: String,
) {
    internal fun parseDates() {
        createdOn = DateUtils.convertToUtcFromServer(createdOn) ?: ""
        endDate = DateUtils.convertToUtcFromServer(endDate) ?: ""
        startDate = DateUtils.convertToUtcFromServer(startDate) ?: ""
    }
}

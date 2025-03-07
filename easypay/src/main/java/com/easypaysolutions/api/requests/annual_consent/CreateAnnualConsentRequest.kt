package com.easypaysolutions.api.requests.annual_consent

import com.easypaysolutions.api.requests.base.ApiRequest
import com.easypaysolutions.api.requests.charge_cc.CreditCardInfoDto
import com.easypaysolutions.api.requests.charge_cc.PersonalDataDto
import com.google.gson.annotations.SerializedName

internal class CreateAnnualConsentRequest(
    override val userDataPresent: Boolean,
    override val body: CreateAnnualConsentBodyDto,
) : ApiRequest<CreateAnnualConsentBodyDto>(userDataPresent, body)

internal data class CreateAnnualConsentBodyDto(

    @SerializedName("ccCardInfo")
    val creditCardInfo: CreditCardInfoDto,

    @SerializedName("AcctHolder")
    val accountHolder: PersonalDataDto,

    // EndCustomer is optional, but API doesn't allow null values - requires empty object.
    @SerializedName("EndCustomer")
    val endCustomer: Any,

    @SerializedName("ConsentCreator")
    val consentCreator: ConsentCreatorDto,
)

internal data class ConsentCreatorDto(
    @SerializedName("MerchID")
    val merchantId: Int,

    @SerializedName("ServiceDescrip")
    val serviceDescription: String? = null,

    @SerializedName("CustomerRefID")
    val customerReferenceId: String? = null,

    @SerializedName("RPGUID")
    val rpguid: String? = null,

    @SerializedName("StartDate")
    val startDate: String,  //Timestamp in milliseconds in format \/Date(1710936735853)\/

    @SerializedName("NumDays")
    val numDays: Int,

    @SerializedName("LimitPerCharge")
    val limitPerCharge: String,

    @SerializedName("LimitLifeTime")
    val limitLifeTime: String,
)

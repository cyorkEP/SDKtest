package com.easypaysolutions.api.requests.charge_cc

import com.easypaysolutions.api.requests.base.ApiRequest
import com.google.gson.annotations.SerializedName

internal data class ChargeCreditCardRequest(
    override val userDataPresent: Boolean,
    override val body: ChargeCreditCardBodyDto,
) : ApiRequest<ChargeCreditCardBodyDto>(userDataPresent, body)

internal data class ChargeCreditCardBodyDto(
    @SerializedName("ccCardInfo")
    val creditCardInfo: CreditCardInfoDto,

    @SerializedName("AcctHolder")
    val accountHolder: PersonalDataDto,

    // EndCustomer is optional, but API doesn't allow null values - requires empty object.
    @SerializedName("EndCustomer")
    val endCustomer: Any,

    @SerializedName("Amounts")
    val amounts: AmountsDto,

    @SerializedName("PurchItems")
    val purchaseItems: PurchaseItemsDto,

    @SerializedName("MerchID")
    val merchantId: Int,
)

internal data class CreditCardInfoDto(
    @SerializedName("AccountNumber")
    val accountNumber: String,

    @SerializedName("ExpMonth")
    val expMonth: Int,

    @SerializedName("ExpYear")
    val expYear: Int,

    @SerializedName("CSV")
    val csv: String,
)

internal data class PersonalDataDto(
    @SerializedName("Firstname")
    val firstName: String? = null,

    @SerializedName("Lastname")
    val lastName: String? = null,

    @SerializedName("Company")
    val company: String? = null,

    @SerializedName("Title")
    val title: String? = null,

    @SerializedName("Url")
    val url: String? = null,

    @SerializedName("BillIngAdress")
    val billingAddress: BillingAddressDto,

    @SerializedName("Email")
    val email: String? = null,

    @SerializedName("Phone")
    val phone: String? = null,
)

internal data class BillingAddressDto(
    @SerializedName("Address1")
    val address1: String,

    @SerializedName("Address2")
    val address2: String? = null,

    @SerializedName("City")
    val city: String? = null,

    @SerializedName("State")
    val state: String? = null,

    @SerializedName("ZIP")
    val zip: String,

    @SerializedName("Country")
    val country: String? = null,
)

internal data class AmountsDto(
    @SerializedName("TotalAmt")
    val totalAmount: String,

    @SerializedName("SalesTax")
    val salesTax: String,

    @SerializedName("Surcharge")
    val surcharge: String,

    @SerializedName("Tip")
    val tip: String,

    @SerializedName("CashBack")
    val cashback: String,

    @SerializedName("ClinicAmount")
    val clinicAmount: String,

    @SerializedName("VisionAmount")
    val visionAmount: String,

    @SerializedName("PrescriptionAmount")
    val prescriptionAmount: String,

    @SerializedName("DentalAmount")
    val dentalAmount: String,

    @SerializedName("TotalMedicalAmount")
    val totalMedicalAmount: String,
)

internal data class PurchaseItemsDto(
    @SerializedName("ServiceDescrip")
    val serviceDescription: String? = null,

    @SerializedName("ClientRefID")
    val clientRefId: String? = null,

    @SerializedName("RPGUID")
    val rpguid: String? = null,
)
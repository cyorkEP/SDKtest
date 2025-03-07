package com.easypaysolutions.repositories.charge_cc

import android.os.Parcelable
import com.easypaysolutions.api.requests.charge_cc.AmountsDto
import com.easypaysolutions.api.requests.charge_cc.BillingAddressDto
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardBodyDto
import com.easypaysolutions.api.requests.charge_cc.CreditCardInfoDto
import com.easypaysolutions.api.requests.charge_cc.PersonalDataDto
import com.easypaysolutions.api.requests.charge_cc.PurchaseItemsDto
import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField
import com.easypaysolutions.utils.secured.SecureData
import com.easypaysolutions.utils.validation.RegexPattern.ADDRESS1
import com.easypaysolutions.utils.validation.RegexPattern.ADDRESS2
import com.easypaysolutions.utils.validation.RegexPattern.CITY
import com.easypaysolutions.utils.validation.RegexPattern.CLIENT_REF_ID_OR_RPGUID
import com.easypaysolutions.utils.validation.RegexPattern.COMPANY
import com.easypaysolutions.utils.validation.RegexPattern.COUNTRY_OR_STATE
import com.easypaysolutions.utils.validation.RegexPattern.EMAIL
import com.easypaysolutions.utils.validation.RegexPattern.FIRST_OR_LAST_NAME
import com.easypaysolutions.utils.validation.RegexPattern.ONLY_NUMBERS
import com.easypaysolutions.utils.validation.RegexPattern.SERVICE_DESCRIPTION
import com.easypaysolutions.utils.validation.RegexPattern.ZIP_CODE
import com.easypaysolutions.utils.validation.ValidateNumberGreaterThanZero
import com.easypaysolutions.utils.validation.ValidateLength
import com.easypaysolutions.utils.validation.ValidateNotBlank
import com.easypaysolutions.utils.validation.ValidateRegex
import kotlinx.parcelize.Parcelize

data class ChargeCreditCardBodyParams(
    val encryptedCardNumber: SecureData<String>,
    val creditCardInfo: CreditCardInfoParam,
    val accountHolder: AccountHolderDataParam,
    val endCustomer: EndCustomerDataParam?,
    val amounts: AmountsParam,
    val purchaseItems: PurchaseItemsParam,
    val merchantId: Int,
) : BaseBodyParams() {
    internal fun toDto(): ChargeCreditCardBodyDto =
        ChargeCreditCardBodyDto(
            creditCardInfo = creditCardInfo.toDto(encryptedCardNumber.data),
            accountHolder = accountHolder.toDto(),
            endCustomer = endCustomer?.toDto() ?: Any(),
            amounts = amounts.toDto(),
            purchaseItems = purchaseItems.toDto(),
            merchantId = merchantId,
        )

    override fun toMappedFields(): List<MappedField> {
        return javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) } +
                creditCardInfo.toMappedFields() +
                accountHolder.toMappedFields() +
                (endCustomer?.toMappedFields() ?: listOf()) +
                amounts.toMappedFields() +
                purchaseItems.toMappedFields()
    }
}

data class CreditCardInfoParam(
    @ValidateNumberGreaterThanZero
    @ValidateLength(maxLength = 2)
    val expMonth: Int,

    @ValidateNumberGreaterThanZero
    @ValidateLength(maxLength = 4)
    val expYear: Int,

    @ValidateLength(maxLength = 4)
    @ValidateNotBlank
    val csv: String,
) : BaseBodyParams() {
    internal fun toDto(encryptedAccountNumber: String): CreditCardInfoDto = CreditCardInfoDto(
        accountNumber = encryptedAccountNumber,
        expMonth = expMonth,
        expYear = expYear,
        csv = csv,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}

@Parcelize
data class AccountHolderDataParam(
    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = FIRST_OR_LAST_NAME)
    val firstName: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = FIRST_OR_LAST_NAME)
    val lastName: String? = null,

    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = COMPANY)
    val company: String? = null,

    val billingAddress: AccountHolderBillingAddressParam,

    @ValidateLength(maxLength = 150)
    @ValidateRegex(regex = EMAIL)
    val email: String? = null,

    @ValidateLength(maxLength = 16)
    @ValidateRegex(regex = ONLY_NUMBERS)
    val phone: String? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): PersonalDataDto = PersonalDataDto(
        firstName = firstName,
        lastName = lastName,
        company = company,
        title = "",
        url = "",
        billingAddress = billingAddress.toDto(),
        email = email,
        phone = phone,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) } +
                billingAddress.toMappedFields()
    }
}

@Parcelize
data class AccountHolderBillingAddressParam(
    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = ADDRESS1)
    @ValidateNotBlank
    val address1: String,

    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = ADDRESS2)
    val address2: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = CITY)
    val city: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = COUNTRY_OR_STATE)
    val state: String? = null,

    @ValidateLength(maxLength = 20)
    @ValidateRegex(regex = ZIP_CODE)
    @ValidateNotBlank
    val zip: String,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = COUNTRY_OR_STATE)
    val country: String? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): BillingAddressDto = BillingAddressDto(
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        zip = zip,
        country = country,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}

@Parcelize
data class EndCustomerDataParam(
    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = FIRST_OR_LAST_NAME)
    val firstName: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = FIRST_OR_LAST_NAME)
    val lastName: String? = null,

    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = COMPANY)
    val company: String? = null,

    val billingAddress: EndCustomerBillingAddressParam,

    @ValidateLength(maxLength = 150)
    @ValidateRegex(regex = EMAIL)
    val email: String? = null,

    @ValidateLength(maxLength = 16)
    @ValidateRegex(regex = ONLY_NUMBERS)
    val phone: String? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): PersonalDataDto = PersonalDataDto(
        firstName = firstName,
        lastName = lastName,
        company = company,
        title = "",
        url = "",
        billingAddress = billingAddress.toDto(),
        email = email,
        phone = phone,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) } +
                billingAddress.toMappedFields()
    }
}

@Parcelize
data class EndCustomerBillingAddressParam(
    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = ADDRESS1)
    val address1: String,

    @ValidateLength(maxLength = 100)
    @ValidateRegex(regex = ADDRESS2)
    val address2: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = CITY)
    val city: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = COUNTRY_OR_STATE)
    val state: String? = null,

    @ValidateLength(maxLength = 20)
    @ValidateRegex(regex = ZIP_CODE)
    val zip: String,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = COUNTRY_OR_STATE)
    val country: String? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): BillingAddressDto = BillingAddressDto(
        address1 = address1,
        address2 = address2,
        city = city,
        state = state,
        zip = zip,
        country = country,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}

@Parcelize
data class AmountsParam(
    @ValidateNumberGreaterThanZero
    val totalAmount: Double,
    val salesTax: Double? = null,
    val surcharge: Double? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): AmountsDto = AmountsDto(
        totalAmount = totalAmount.toBigDecimal().toPlainString(),
        salesTax = (salesTax ?: 0.0).toBigDecimal().toPlainString(),
        surcharge = (surcharge ?: 0.0).toBigDecimal().toPlainString(),
        tip = 0.0.toBigDecimal().toPlainString(),
        cashback = 0.0.toBigDecimal().toPlainString(),
        clinicAmount = 0.0.toBigDecimal().toPlainString(),
        visionAmount = 0.0.toBigDecimal().toPlainString(),
        prescriptionAmount = 0.0.toBigDecimal().toPlainString(),
        dentalAmount = 0.0.toBigDecimal().toPlainString(),
        totalMedicalAmount = 0.0.toBigDecimal().toPlainString(),
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}

@Parcelize
data class PurchaseItemsParam(
    @ValidateLength(maxLength = 200)
    @ValidateRegex(regex = SERVICE_DESCRIPTION)
    val serviceDescription: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = CLIENT_REF_ID_OR_RPGUID)
    val clientRefId: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = CLIENT_REF_ID_OR_RPGUID)
    val rpguid: String? = null,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): PurchaseItemsDto = PurchaseItemsDto(
        serviceDescription = serviceDescription,
        clientRefId = clientRefId,
        rpguid = rpguid,
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}
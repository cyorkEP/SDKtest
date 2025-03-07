package com.easypaysolutions.repositories.annual_consent.create

import android.os.Parcelable
import com.easypaysolutions.api.requests.annual_consent.ConsentCreatorDto
import com.easypaysolutions.api.requests.annual_consent.CreateAnnualConsentBodyDto
import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.CreditCardInfoParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.utils.DateUtils
import com.easypaysolutions.utils.secured.SecureData
import com.easypaysolutions.utils.validation.RegexPattern
import com.easypaysolutions.utils.validation.ValidateNumberGreaterThanZero
import com.easypaysolutions.utils.validation.ValidateLength
import com.easypaysolutions.utils.validation.ValidateNotBlank
import com.easypaysolutions.utils.validation.ValidateRegex
import kotlinx.parcelize.Parcelize
import java.util.Date

data class CreateAnnualConsentBodyParams(
    val last4digits: String,
    val encryptedCardNumber: SecureData<String>,
    val creditCardInfo: CreditCardInfoParam,
    val accountHolder: AccountHolderDataParam,
    val endCustomer: EndCustomerDataParam?,
    val consentCreator: ConsentCreatorParam,
) : BaseBodyParams() {
    internal fun toDto(): CreateAnnualConsentBodyDto = CreateAnnualConsentBodyDto(
        creditCardInfo = creditCardInfo.toDto(encryptedCardNumber.data),
        accountHolder = accountHolder.toDto(),
        endCustomer = endCustomer?.toDto() ?: Any(),
        consentCreator = consentCreator.toDto(),
    )

    override fun toMappedFields(): List<MappedField> {
        return javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) } +
                creditCardInfo.toMappedFields() +
                accountHolder.toMappedFields() +
                (endCustomer?.toMappedFields() ?: listOf()) +
                consentCreator.toMappedFields()
    }
}

@Parcelize
data class ConsentCreatorParam(
    val merchantId: Int,

    @ValidateLength(maxLength = 200)
    @ValidateRegex(regex = RegexPattern.SERVICE_DESCRIPTION)
    val serviceDescription: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = RegexPattern.CLIENT_REF_ID_OR_RPGUID)
    val customerReferenceId: String? = null,

    @ValidateLength(maxLength = 75)
    @ValidateRegex(regex = RegexPattern.CLIENT_REF_ID_OR_RPGUID)
    val rpguid: String? = null,
    val startDate: Date,

    @ValidateNumberGreaterThanZero
    val limitPerCharge: Double,

    @ValidateNumberGreaterThanZero
    val limitLifeTime: Double,
) : BaseBodyParams(), Parcelable {
    internal fun toDto(): ConsentCreatorDto = ConsentCreatorDto(
        merchantId = merchantId,
        serviceDescription = serviceDescription,
        customerReferenceId = customerReferenceId,
        rpguid = rpguid,
        startDate = DateUtils.parseDateForApi(startDate),
        numDays = 365,
        limitPerCharge = limitPerCharge.toBigDecimal().toPlainString(),
        limitLifeTime = limitLifeTime.toBigDecimal().toPlainString(),
    )

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}
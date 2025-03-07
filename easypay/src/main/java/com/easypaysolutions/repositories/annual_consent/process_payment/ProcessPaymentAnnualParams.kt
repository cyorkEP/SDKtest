package com.easypaysolutions.repositories.annual_consent.process_payment

import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualBodyDto
import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField
import com.easypaysolutions.utils.validation.ValidateNumberGreaterThanZero

data class ProcessPaymentAnnualParams(
    val consentId: Int,

    @ValidateNumberGreaterThanZero
    val processAmount: Double,
) : BaseBodyParams() {
    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }

    internal fun toDto(): ProcessPaymentAnnualBodyDto = ProcessPaymentAnnualBodyDto(
        consentId = consentId,
        processAmount = processAmount.toBigDecimal().toPlainString(),
    )
}
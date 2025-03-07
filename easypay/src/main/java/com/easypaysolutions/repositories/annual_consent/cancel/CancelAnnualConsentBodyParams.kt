package com.easypaysolutions.repositories.annual_consent.cancel

import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentBodyDto
import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField

data class CancelAnnualConsentBodyParams(val consentId: Int) : BaseBodyParams() {

    internal fun toDto(): CancelAnnualConsentBodyDto = CancelAnnualConsentBodyDto(consentId)

    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}
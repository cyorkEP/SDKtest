package com.easypaysolutions.repositories.annual_consent.list

import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField

data class ListAnnualConsentsBodyParams(
    val merchantId: Int? = null,
    val customerReferenceId: String? = null,
    val rpguid: String? = null
): BaseBodyParams() {
    override fun toMappedFields(): List<MappedField> {
        return this.javaClass.declaredFields.toList().map { MappedField(it, it.get(this)) }
    }
}
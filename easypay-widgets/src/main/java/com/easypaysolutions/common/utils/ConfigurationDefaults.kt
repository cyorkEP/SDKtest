package com.easypaysolutions.common.utils

import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.repositories.charge_cc.PurchaseItemsParam
import java.util.Date

internal object ConfigurationDefaults {
    const val merchantId: Int = 0
    val preselectedCardId: Int? = null
    val accountHolder: AccountHolderDataParam? = null
    val endCustomer: EndCustomerDataParam? = null
    val amounts: AmountsParam = AmountsParam(totalAmount = 0.0)
    val purchaseItems: PurchaseItemsParam? = null
    val consentCreator: ConsentCreatorParam = ConsentCreatorParam(
        merchantId = merchantId,
        startDate = Date(),
        limitPerCharge = 0.0,
        limitLifeTime = 0.0,
        customerReferenceId = ""
    )
}

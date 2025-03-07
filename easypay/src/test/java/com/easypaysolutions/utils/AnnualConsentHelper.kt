package com.easypaysolutions.utils

import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsentBodyParams
import com.easypaysolutions.utils.ChargeCreditCardHelper.prepareAccountHolder
import com.easypaysolutions.utils.ChargeCreditCardHelper.prepareCreditCardInfo
import com.easypaysolutions.utils.ChargeCreditCardHelper.prepareEndCustomer
import com.easypaysolutions.utils.secured.SecureData
import java.util.Date

internal object AnnualConsentHelper {
    fun prepareParams(
        secureData: SecureData<String>,
        zip: String = "04005",
        limitPerCharge: Double = 10.0,
        csv: String = "999",
        customerRefId: String = "Client Ref Id",
        rpguid: String = "RPGUID",
    ): CreateAnnualConsentBodyParams =
        CreateAnnualConsentBodyParams(
            encryptedCardNumber = secureData,
            creditCardInfo = prepareCreditCardInfo(csv),
            accountHolder = prepareAccountHolder(zip),
            endCustomer = prepareEndCustomer(),
            consentCreator = prepareConsentCreator(limitPerCharge, customerRefId, rpguid),
        )

    private fun prepareConsentCreator(
        limitPerCharge: Double,
        customerRefId: String,
        rpguid: String,
    ): ConsentCreatorParam =
        ConsentCreatorParam(
            merchantId = 1,
            serviceDescription = "Service Description",
            customerReferenceId = customerRefId,
            rpguid = rpguid,
            startDate = Date(),
            limitPerCharge = limitPerCharge,
            limitLifeTime = 100.0
        )
}
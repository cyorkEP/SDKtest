package com.easypaysolutions.common.presentation.add_new_card

import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsentBodyParams
import com.easypaysolutions.repositories.charge_cc.AccountHolderBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCardBodyParams
import com.easypaysolutions.repositories.charge_cc.CreditCardInfoParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.repositories.charge_cc.PurchaseItemsParam

internal object AddNewCardHelper {

    fun toCreateAnnualConsentBodyParams(
        viewData: AddNewCardViewData,
        endCustomer: EndCustomerDataParam?,
        accountHolder: AccountHolderDataParam?,
        consentCreator: ConsentCreatorParam,
    ): CreateAnnualConsentBodyParams {
        return CreateAnnualConsentBodyParams(
            last4digits = viewData.last4digits,
            encryptedCardNumber = viewData.encryptedCardNumber,
            creditCardInfo = prepareCreditCardInfo(viewData),
            accountHolder = prepareAccountHolder(viewData, accountHolder),
            endCustomer = endCustomer,
            consentCreator = prepareConsentCreator(consentCreator),
        )
    }

    fun toChargeCreditCardBodyParams(
        viewData: AddNewCardViewData,
        endCustomer: EndCustomerDataParam?,
        accountHolder: AccountHolderDataParam?,
        amounts: AmountsParam,
        purchaseItems: PurchaseItemsParam?,
        consentCreator: ConsentCreatorParam,
    ): ChargeCreditCardBodyParams {
        return ChargeCreditCardBodyParams(
            encryptedCardNumber = viewData.encryptedCardNumber,
            creditCardInfo = prepareCreditCardInfo(viewData),
            accountHolder = prepareAccountHolder(viewData, accountHolder),
            endCustomer = endCustomer,
            amounts = amounts,
            purchaseItems = purchaseItems ?: PurchaseItemsParam(),
            merchantId = consentCreator.merchantId,
        )
    }

    private fun prepareConsentCreator(consentCreator: ConsentCreatorParam): ConsentCreatorParam {
        return ConsentCreatorParam(
            merchantId = consentCreator.merchantId,
            startDate = consentCreator.startDate,
            limitPerCharge = consentCreator.limitPerCharge,
            limitLifeTime = consentCreator.limitLifeTime,
            serviceDescription = consentCreator.serviceDescription,
            customerReferenceId = consentCreator.customerReferenceId,
            rpguid = consentCreator.rpguid,
        )
    }

    private fun prepareAccountHolder(
        viewData: AddNewCardViewData,
        accountHolder: AccountHolderDataParam?,
    ): AccountHolderDataParam {
        return AccountHolderDataParam(
            firstName = viewData.cardHolderName,
            lastName = "",
            email = accountHolder?.email,
            phone = accountHolder?.phone,
            company = accountHolder?.company,

            billingAddress = AccountHolderBillingAddressParam(
                address1 = viewData.streetAddress,
                address2 = accountHolder?.billingAddress?.address2,
                city = accountHolder?.billingAddress?.city,
                state = accountHolder?.billingAddress?.state,
                zip = viewData.zip,
                country = accountHolder?.billingAddress?.country,
            )
        )
    }

    private fun prepareCreditCardInfo(viewData: AddNewCardViewData): CreditCardInfoParam {
        return CreditCardInfoParam(
            expMonth = viewData.expMonth.toIntOrNull() ?: 0,
            expYear = viewData.expYear.toIntOrNull() ?: 0,
            csv = viewData.cvv,
        )
    }

}
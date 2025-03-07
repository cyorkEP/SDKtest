package com.easypay_sample.ui.charge_card

import com.easypaysolutions.repositories.charge_cc.AccountHolderBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCardBodyParams
import com.easypaysolutions.repositories.charge_cc.CreditCardInfoParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.repositories.charge_cc.PurchaseItemsParam
import com.easypaysolutions.utils.secured.SecureData
import com.easypay_sample.utils.toNullIfBlank

data class ChargeCardViewData(
    var merchantId: String? = null,
    var expMonth: String? = null,
    var expYear: String? = null,
    var cvv: String? = null,
    var holderFirstName: String? = null,
    var holderLastName: String? = null,
    var holderCompany: String? = null,
    var holderPhone: String? = null,
    var holderEmail: String? = null,
    var holderAddress1: String? = null,
    var holderAddress2: String? = null,
    var holderCity: String? = null,
    var holderState: String? = null,
    var holderZip: String? = null,
    var holderCountry: String? = null,
    var customerFirstName: String? = null,
    var customerLastName: String? = null,
    var customerCompany: String? = null,
    var customerPhone: String? = null,
    var customerEmail: String? = null,
    var customerAddress1: String? = null,
    var customerAddress2: String? = null,
    var customerCity: String? = null,
    var customerState: String? = null,
    var customerZip: String? = null,
    var customerCountry: String? = null,
    var totalAmount: String? = null,
    var salesAmount: String? = null,
    var surcharge: String? = null,
    var serviceDescription: String? = null,
    var clientRefId: String? = null,
    var rpguid: String? = null,
) {

    //region Public

    fun toChargeCreditCardBodyParams(secureData: SecureData<String>): ChargeCreditCardBodyParams {
        return ChargeCreditCardBodyParams(
            encryptedCardNumber = secureData,
            creditCardInfo = prepareCreditCardInfo(),
            accountHolder = prepareAccountHolder(),
            endCustomer = prepareEndCustomer(),
            amounts = prepareAmounts(),
            purchaseItems = preparePurchaseItems(),
            merchantId = merchantId?.toIntOrNull() ?: 0
        )
    }

    //endregion

    //region Private

    private fun preparePurchaseItems(): PurchaseItemsParam {
        return PurchaseItemsParam(
            serviceDescription = serviceDescription?.toNullIfBlank(),
            clientRefId = clientRefId?.toNullIfBlank(),
            rpguid = rpguid?.toNullIfBlank()
        )
    }

    private fun prepareAmounts(): AmountsParam {
        return AmountsParam(
            totalAmount = totalAmount?.toDoubleOrNull() ?: 0.0,
            salesTax = salesAmount?.toDoubleOrNull(),
            surcharge = surcharge?.toDoubleOrNull(),
        )
    }

    private fun prepareEndCustomer(): EndCustomerDataParam? {
        if (customerFirstName.isNullOrEmpty() &&
            customerLastName.isNullOrEmpty() &&
            customerCompany.isNullOrEmpty() &&
            customerEmail.isNullOrEmpty() &&
            customerPhone.isNullOrEmpty() &&
            customerAddress1.isNullOrEmpty() &&
            customerAddress2.isNullOrEmpty() &&
            customerZip.isNullOrEmpty() &&
            customerCountry.isNullOrEmpty() &&
            customerCity.isNullOrEmpty() &&
            customerState.isNullOrEmpty()
        ) {
            return null
        }
        return EndCustomerDataParam(
            firstName = customerFirstName?.toNullIfBlank(),
            lastName = customerLastName?.toNullIfBlank(),
            company = customerCompany?.toNullIfBlank(),
            billingAddress = prepareEndCustomerBillingAddress(
                customerAddress1,
                customerAddress2,
                customerCity,
                customerState,
                customerZip,
                customerCountry
            ),
            email = customerEmail?.toNullIfBlank(),
            phone = customerPhone?.toNullIfBlank()
        )
    }

    private fun prepareAccountHolder(): AccountHolderDataParam {
        return AccountHolderDataParam(
            firstName = holderFirstName?.toNullIfBlank(),
            lastName = holderLastName?.toNullIfBlank(),
            company = holderCompany?.toNullIfBlank(),
            billingAddress = prepareAccountHolderBillingAddress(
                holderAddress1,
                holderAddress2,
                holderCity,
                holderState,
                holderZip,
                holderCountry
            ),
            email = holderEmail?.toNullIfBlank(),
            phone = holderPhone?.toNullIfBlank()
        )
    }

    private fun prepareAccountHolderBillingAddress(
        address1: String?,
        address2: String?,
        city: String?,
        state: String?,
        zip: String?,
        country: String?,
    ): AccountHolderBillingAddressParam {
        return AccountHolderBillingAddressParam(
            address1 = address1 ?: "",
            address2 = address2?.toNullIfBlank(),
            city = city?.toNullIfBlank(),
            state = state?.toNullIfBlank(),
            zip = zip ?: "",
            country = country?.toNullIfBlank()
        )
    }

    private fun prepareEndCustomerBillingAddress(
        address1: String?,
        address2: String?,
        city: String?,
        state: String?,
        zip: String?,
        country: String?,
    ): EndCustomerBillingAddressParam {
        return EndCustomerBillingAddressParam(
            address1 = address1 ?: "",
            address2 = address2?.toNullIfBlank(),
            city = city?.toNullIfBlank(),
            state = state?.toNullIfBlank(),
            zip = zip ?: "",
            country = country?.toNullIfBlank()
        )
    }

    private fun prepareCreditCardInfo(): CreditCardInfoParam {
        return CreditCardInfoParam(
            expMonth = expMonth?.toIntOrNull() ?: 0,
            expYear = expYear?.toIntOrNull() ?: 0,
            csv = cvv ?: ""
        )
    }

    //endregion

}
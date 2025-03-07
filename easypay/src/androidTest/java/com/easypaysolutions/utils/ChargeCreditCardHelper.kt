package com.easypaysolutions.utils

import com.easypaysolutions.repositories.charge_cc.AccountHolderBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCardBodyParams
import com.easypaysolutions.repositories.charge_cc.CreditCardInfoParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.repositories.charge_cc.PurchaseItemsParam
import com.easypaysolutions.utils.secured.SecureData

internal object ChargeCreditCardHelper {
    fun prepareParams(
        secureData: SecureData<String>,
        zip: String = "04005",
        totalAmount: Double = 10.0,
        csv: String = "999",
    ): ChargeCreditCardBodyParams = ChargeCreditCardBodyParams(
        encryptedCardNumber = secureData,
        creditCardInfo = prepareCreditCardInfo(csv),
        accountHolder = prepareAccountHolder(zip),
        endCustomer = prepareEndCustomer(),
        amounts = prepareAmounts(totalAmount),
        purchaseItems = preparePurchaseItems(),
        merchantId = 1
    )

    fun prepareCreditCardInfo(csv: String): CreditCardInfoParam = CreditCardInfoParam(
        expMonth = 10,
        expYear = 2026,
        csv = csv
    )

    fun prepareAccountHolder(zip: String): AccountHolderDataParam = AccountHolderDataParam(
        firstName = "John",
        lastName = "Doe",
        company = "",
        billingAddress = prepareAccountHolderBillingAddress(zip),
        email = "robert@easypaysolutions.com",
        phone = "8775558472"
    )

    fun prepareEndCustomer(): EndCustomerDataParam = EndCustomerDataParam(
        firstName = "John",
        lastName = "Doe",
        company = "",
        billingAddress = prepareEndCustomerBillingAddress(),
        email = "robert@easypaysolutions.com",
        phone = "8775558472"
    )

    private fun preparePurchaseItems(): PurchaseItemsParam = PurchaseItemsParam(
        serviceDescription = "FROM API TESTER",
        clientRefId = "12456",
        rpguid = "3d3424a6-c5f3-4c28"
    )

    private fun prepareAmounts(totalAmount: Double): AmountsParam = AmountsParam(
        totalAmount = totalAmount,
        salesTax = 0.0,
        surcharge = 0.0,
    )

    private fun prepareAccountHolderBillingAddress(
        zip: String = "04005",
    ): AccountHolderBillingAddressParam = AccountHolderBillingAddressParam(
        address1 = "123 Fake St.",
        address2 = "",
        city = "PORTLAND",
        state = "ME",
        zip = zip,
        country = "USA"
    )

    private fun prepareEndCustomerBillingAddress(): EndCustomerBillingAddressParam =
        EndCustomerBillingAddressParam(
            address1 = "123 Fake St.",
            address2 = "",
            city = "PORTLAND",
            state = "ME",
            zip = "04005",
            country = "USA"
        )
}
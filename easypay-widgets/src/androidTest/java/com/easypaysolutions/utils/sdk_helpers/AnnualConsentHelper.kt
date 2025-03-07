package com.easypaysolutions.utils.sdk_helpers

import android.content.Context
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsentBodyParams
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsent
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsentBodyParams
import com.easypaysolutions.repositories.charge_cc.AccountHolderBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.CreditCardInfoParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.utils.secured.SecureData
import com.easypaysolutions.utils.secured.SecureTextField
import java.util.Date

internal object AnnualConsentHelper {

    suspend fun createAnnualConsent(context: Context): CreateAnnualConsentResult {
        val secureTextField = SecureTextField(context)
        secureTextField.setText("4242424242424242")
        val params = prepareParams(secureTextField.secureData)
        val result = CreateAnnualConsent().createAnnualConsent(params)
        return result.data ?: throw IllegalStateException("Failed to create annual consent")
    }

    suspend fun cancelAnnualConsent(consentId: Int) {
        val params = CancelAnnualConsentBodyParams(consentId)
        CancelAnnualConsent().cancelAnnualConsent(params)
    }

    private fun prepareParams(
        secureData: SecureData<String>,
        zip: String = "04005",
        limitPerCharge: Double = 10000.0,
        csv: String = "999",
    ): CreateAnnualConsentBodyParams =
        CreateAnnualConsentBodyParams(
            encryptedCardNumber = secureData,
            creditCardInfo = prepareCreditCardInfo(csv),
            accountHolder = prepareAccountHolder(zip),
            endCustomer = prepareEndCustomer(),
            consentCreator = prepareConsentCreator(limitPerCharge),
        )

    private fun prepareConsentCreator(limitPerCharge: Double): ConsentCreatorParam =
        ConsentCreatorParam(
            merchantId = 1,
            serviceDescription = "Service Description",
            customerReferenceId = "Client Ref Id",
            rpguid = "RPGUID",
            startDate = Date(),
            limitPerCharge = limitPerCharge,
            limitLifeTime = 100000.0
        )

    private fun prepareCreditCardInfo(csv: String): CreditCardInfoParam = CreditCardInfoParam(
        expMonth = 10,
        expYear = 2026,
        csv = csv
    )

    private fun prepareAccountHolder(zip: String): AccountHolderDataParam = AccountHolderDataParam(
        firstName = "John",
        lastName = "Doe",
        company = "",
        billingAddress = prepareAccountHolderBillingAddress(zip),
        email = "robert@easypaysolutions.com",
        phone = "8775558472"
    )

    private fun prepareEndCustomer(): EndCustomerDataParam = EndCustomerDataParam(
        firstName = "John",
        lastName = "Doe",
        company = "",
        billingAddress = prepareEndCustomerBillingAddress(),
        email = "robert@easypaysolutions.com",
        phone = "8775558472"
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
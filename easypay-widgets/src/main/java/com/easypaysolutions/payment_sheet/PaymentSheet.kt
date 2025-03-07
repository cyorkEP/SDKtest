package com.easypaysolutions.payment_sheet

import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.easypaysolutions.common.utils.ConfigurationDefaults
import com.easypaysolutions.payment_sheet.utils.DefaultPaymentSheetLauncher
import com.easypaysolutions.payment_sheet.utils.PaymentSheetLauncher
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResultCallback
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import com.easypaysolutions.repositories.charge_cc.PurchaseItemsParam
import kotlinx.parcelize.Parcelize

/**
 * Entry-point to the EasyPay PaymentSheet Widget.
 */
class PaymentSheet internal constructor(
    private val launcher: PaymentSheetLauncher,
) {

    constructor(
        activity: ComponentActivity,
        callback: PaymentSheetResultCallback,
    ) : this(
        DefaultPaymentSheetLauncher(activity, callback)
    )

    constructor(
        fragment: Fragment,
        callback: PaymentSheetResultCallback,
    ) : this(
        DefaultPaymentSheetLauncher(fragment, callback)
    )

    fun present(configuration: Configuration? = null) {
        launcher.present(configuration)
    }

    /** Configuration for [PaymentSheet] **/
    @Parcelize
    data class Configuration(

        /**
         * (Optional) Preselected card ID.
         */
        val preselectedCardId: Int? = null,

        /**
         * Consent Creator data - used in case that the user agrees to save the card for future payments.
         * Required to pass:
         * - [ConsentCreatorParam.merchantId];
         * - [ConsentCreatorParam.customerReferenceId] or [ConsentCreatorParam.rpguid];
         * - [ConsentCreatorParam.limitPerCharge];
         * - [ConsentCreatorParam.limitLifeTime].
         */
        val consentCreator: ConsentCreatorParam = ConfigurationDefaults.consentCreator,

        /**
         * (Optional) Account holder data.
         * Widget will override the following fields:
         * - [AccountHolderDataParam.firstName];
         * - [AccountHolderDataParam.lastName] with empty String;
         * - [AccountHolderDataParam.billingAddress.address1];
         * - [AccountHolderDataParam.billingAddress.zip].
         */
        val accountHolder: AccountHolderDataParam? = ConfigurationDefaults.accountHolder,

        /**
         * (Optional) End Customer data.
         */
        val endCustomer: EndCustomerDataParam? = ConfigurationDefaults.endCustomer,

        /**
         * Amounts data with required totalAmount field.
         */
        val amounts: AmountsParam = ConfigurationDefaults.amounts,

        /**
         * (Optional) Purchase items data.
         */
        val purchaseItems: PurchaseItemsParam? = ConfigurationDefaults.purchaseItems,
    ) : Parcelable {

        class Builder {

            private var preselectedCardId: Int? = ConfigurationDefaults.preselectedCardId
            private var consentCreator: ConsentCreatorParam =  ConfigurationDefaults.consentCreator
            private var accountHolder: AccountHolderDataParam? = ConfigurationDefaults.accountHolder
            private var endCustomer: EndCustomerDataParam? = ConfigurationDefaults.endCustomer
            private var amounts: AmountsParam = ConfigurationDefaults.amounts
            private var purchaseItems: PurchaseItemsParam? = ConfigurationDefaults.purchaseItems

            fun setPreselectedCardId(preselectedCardId: Int?): Builder = apply {
                this.preselectedCardId = preselectedCardId
            }

            fun setConsentCreator(consentCreator: ConsentCreatorParam): Builder = apply {
                this.consentCreator = consentCreator
            }

            fun setAccountHolder(accountHolder: AccountHolderDataParam?): Builder = apply {
                this.accountHolder = accountHolder
            }

            fun setEndCustomer(endCustomer: EndCustomerDataParam?): Builder = apply {
                this.endCustomer = endCustomer
            }

            fun setAmounts(amounts: AmountsParam): Builder = apply {
                this.amounts = amounts
            }

            fun setPurchaseItems(purchaseItems: PurchaseItemsParam?): Builder = apply {
                this.purchaseItems = purchaseItems
            }

            fun build(): Configuration {
                return Configuration(
                    preselectedCardId = preselectedCardId,
                    consentCreator = consentCreator,
                    accountHolder = accountHolder,
                    endCustomer = endCustomer,
                    amounts = amounts,
                    purchaseItems = purchaseItems,
                )
            }
        }
    }
}
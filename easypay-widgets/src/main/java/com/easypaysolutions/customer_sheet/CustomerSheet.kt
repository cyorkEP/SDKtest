package com.easypaysolutions.customer_sheet

import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.easypaysolutions.common.utils.ConfigurationDefaults
import com.easypaysolutions.customer_sheet.utils.CustomerSheetLauncher
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResultCallback
import com.easypaysolutions.customer_sheet.utils.DefaultCustomerSheetLauncher
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.EndCustomerDataParam
import kotlinx.parcelize.Parcelize

/**
 * Entry-point to the EasyPay CustomerSheet Widget.
 */
class CustomerSheet internal constructor(
    private val launcher: CustomerSheetLauncher,
) {

    constructor(
        activity: ComponentActivity,
        callback: CustomerSheetResultCallback,
    ) : this(
        DefaultCustomerSheetLauncher(activity, callback)
    )

    constructor(
        fragment: Fragment,
        callback: CustomerSheetResultCallback,
    ) : this(
        DefaultCustomerSheetLauncher(fragment, callback)
    )

    fun present(configuration: Configuration? = null) {
        launcher.present(configuration)
    }

    /** Configuration for [CustomerSheet] **/
    @Parcelize
    data class Configuration internal constructor(

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
    ) : Parcelable {

        class Builder {

            private var preselectedCardId: Int? = ConfigurationDefaults.preselectedCardId
            private var consentCreator: ConsentCreatorParam = ConfigurationDefaults.consentCreator
            private var accountHolder: AccountHolderDataParam? = ConfigurationDefaults.accountHolder
            private var endCustomer: EndCustomerDataParam? = ConfigurationDefaults.endCustomer

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

            fun build(): Configuration {
                return Configuration(
                    preselectedCardId = preselectedCardId,
                    consentCreator = consentCreator,
                    accountHolder = accountHolder,
                    endCustomer = endCustomer,
                )
            }
        }
    }
}
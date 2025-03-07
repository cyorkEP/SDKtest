package com.easypay_sample.ui.customer_sheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.easypay_sample.R
import com.easypay_sample.databinding.FragmentCustomerSheetBinding
import com.easypay_sample.utils.AlertUtils
import com.easypay_sample.utils.viewBinding
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderBillingAddressParam
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class CustomerSheetFragment : Fragment() {

    private val binding by viewBinding(FragmentCustomerSheetBinding::bind)
    private lateinit var customerSheet: CustomerSheet

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheet = CustomerSheet(this, ::onCustomerSheetResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    //endregion

    //region Components initialization

    private fun initComponents() {
        binding.btnCustomerSheet.setOnClickListener {
            presentCustomerSheet()
        }
    }

    //endregion

    //region CustomerSheet

    private fun presentCustomerSheet() {
        val config = prepareConfig()
        customerSheet.present(config)
    }

    private fun onCustomerSheetResult(customerSheetResult: CustomerSheetResult) {
        when (customerSheetResult) {
            is CustomerSheetResult.Failed -> {
                AlertUtils.showAlert(requireContext(), "Error: ${customerSheetResult.error}")
            }

            is CustomerSheetResult.Selected -> {
                Log.d("TEST_RESULT","Deleted consent IDs: ${customerSheetResult.deletedConsents}")
                Log.d("TEST_RESULT","Added consents: ${customerSheetResult.addedConsents.size}")
                AlertUtils.showAlert(
                    requireContext(), "Selected: ${customerSheetResult.selectedConsentId}"
                )
            }
        }
    }

    //endregion

    //region Helpers

    private fun prepareConfig(): CustomerSheet.Configuration {
        val customerRefId = binding.etCustomerRefId.text.toString()
        val rpguid = binding.etRpguid.text.toString()

        val consentCreator = ConsentCreatorParam(
            limitLifeTime = 100000.0,
            limitPerCharge = 1000.0,
            merchantId = 1,
            startDate = Date(),
            customerReferenceId = customerRefId,
            rpguid = rpguid
        )
        val abc = AccountHolderBillingAddressParam(
            address1 = "A1",
            address2 = null,
            city = "Newark",
            state = "AZ",
            zip = "90210",
            country = null
        )
        val acc = AccountHolderDataParam(
            firstName = "fname",
            lastName = "lname",
            company = null,
            billingAddress = abc,
            email = null,
            phone = null
        )
        return CustomerSheet.Configuration
            .Builder()
            .setConsentCreator(consentCreator)
            .setAccountHolder(acc)
            .build()
    }

    //endregion

}
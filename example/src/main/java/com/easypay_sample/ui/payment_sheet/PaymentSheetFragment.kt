package com.easypay_sample.ui.payment_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.easypay_sample.R
import com.easypay_sample.databinding.FragmentPaymentSheetBinding
import com.easypay_sample.utils.AlertUtils
import com.easypay_sample.utils.viewBinding
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class PaymentSheetFragment : Fragment() {

    private val binding by viewBinding(FragmentPaymentSheetBinding::bind)
    private lateinit var paymentSheet: PaymentSheet

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_payment_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    //endregion

    //region Components initialization

    private fun initComponents() {
        binding.btnPaymentSheet.setOnClickListener {
            presentPaymentSheet()
        }
    }

    //endregion

    //region PaymentSheet

    private fun presentPaymentSheet() {
        val config = prepareConfig()
        paymentSheet.present(config)
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Failed -> {
                AlertUtils.showAlert(requireContext(), "Error: ${paymentSheetResult.error}")
            }

            is PaymentSheetResult.Completed -> {
                AlertUtils.showAlert(
                    requireContext(),
                    "Completed with TxID: ${paymentSheetResult.data.txId}"
                )
            }

            is PaymentSheetResult.Canceled -> {
                AlertUtils.showAlert(requireContext(), "Canceled")
            }
        }
    }

    //endregion

    //region Helpers

    private fun prepareConfig(): PaymentSheet.Configuration {
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
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

        return PaymentSheet.Configuration
            .Builder()
            .setAmounts(AmountsParam(amount))
            .setConsentCreator(consentCreator)
            .build()
    }

    //endregion

}
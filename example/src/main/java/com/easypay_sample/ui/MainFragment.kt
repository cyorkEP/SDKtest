package com.easypay_sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.easypay_sample.R
import com.easypay_sample.databinding.FragmentMainBinding
import com.easypay_sample.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        binding.apply {
            btnPrefilledChargeCard.setOnClickListener {
                navigateToChargeCard(prefilled = true)
            }
            btnChargeCard.setOnClickListener {
                navigateToChargeCard()
            }
            btnPrefilledConsentAnnual.setOnClickListener {
                navigateToConsentAnnual(prefilled = true)
            }
            btnConsentAnnual.setOnClickListener {
                navigateToConsentAnnual()
            }
            btnPaymentSheet.setOnClickListener {
                navigateToPaymentSheet()
            }
            btnCustomerSheet.setOnClickListener {
                navigateToCustomerSheet()
            }
        }
    }

    private fun navigateToChargeCard(prefilled: Boolean = false) {
        val action = MainFragmentDirections.actionMainFragmentToChargeCardFragment(prefilled)
        findNavController().navigate(action)
    }

    private fun navigateToConsentAnnual(prefilled: Boolean = false) {
        val action = MainFragmentDirections.actionMainFragmentToConsentAnnualFragment(prefilled)
        findNavController().navigate(action)
    }

    private fun navigateToPaymentSheet() {
        val action = MainFragmentDirections.actionMainFragmentToPaymentSheetFragment()
        findNavController().navigate(action)
    }

    private fun navigateToCustomerSheet() {
        val action = MainFragmentDirections.actionMainFragmentToCustomerSheetFragment()
        findNavController().navigate(action)
    }
}
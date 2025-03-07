package com.easypay_sample.ui.charge_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.easypay_sample.R
import com.easypay_sample.databinding.FragmentChargeCardBinding
import com.easypay_sample.utils.AlertUtils
import com.easypay_sample.utils.ViewState
import com.easypay_sample.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChargeCardFragment : Fragment() {

    private val binding by viewBinding(FragmentChargeCardBinding::bind)
    private val viewModel: ChargeCardViewModel by viewModels { defaultViewModelProviderFactory }

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFlows()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_charge_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isPrefilled =
            arguments?.let { ChargeCardFragmentArgs.fromBundle(it).isPrefilled } ?: false
        viewModel.setupViewData(isPrefilled)
        initComponents()
    }

    //endregion

    //region Flows

    private fun initFlows() {
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.chargeCreditCardResult.collect {
                    when (it) {
                        is ViewState.Loading -> binding.progressView.show()
                        is ViewState.Success -> {
                            it.value?.let { result ->
                                AlertUtils.showAlert(requireContext(), result.responseMessage)
                            }
                            binding.progressView.hide()
                        }

                        is ViewState.Error -> {
                            binding.progressView.hide()
                            AlertUtils.showAlert(requireContext(), it.message)
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Components

    private fun initComponents() {
        binding.apply {
            layoutFields.data = viewModel.viewData
            btnChargeCard.setOnClickListener {
                val secureData = layoutFields.stfCardNumber.secureData
                viewModel.chargeCreditCard(secureData)
            }
        }
    }

    //endregion

}
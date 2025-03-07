package com.easypay_sample.ui.consent_annual.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.easypay_sample.R
import com.easypay_sample.databinding.FragmentAddAnnualConsentBinding
import com.easypay_sample.utils.AlertUtils
import com.easypay_sample.utils.ViewState
import com.easypay_sample.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAnnualConsentFragment : Fragment() {

    private val binding by viewBinding(FragmentAddAnnualConsentBinding::bind)
    private val viewModel: AddAnnualConsentViewModel by viewModels { defaultViewModelProviderFactory }

    //region Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFlows()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add_annual_consent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isPrefilled =
            arguments?.let { AddAnnualConsentFragmentArgs.fromBundle(it).isPrefilled } ?: false
        viewModel.setupViewData(isPrefilled)
        initComponents()
    }

    //endregion

    //region Flows

    private fun initFlows() {
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.createAnnualConsentResult.collect {
                    when (it) {
                        is ViewState.Loading -> binding.progressView.show()
                        is ViewState.Success -> {
                            binding.progressView.hide()
                            it.value?.let { result ->
                                AlertUtils.showAlert(requireContext(), result.responseMessage)
                                navigateBack()
                            }
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
                viewModel.createAnnualConsent(secureData)
            }
        }
    }

    //endregion

    //region Navigation

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    //endregion

}
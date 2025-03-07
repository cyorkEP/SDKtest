package com.easypaysolutions.common.presentation.add_new_card

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easypaysolutions.common.exceptions.EasyPayWidgetException
import com.easypaysolutions.common.presentation.AddNewCardUiState
import com.easypaysolutions.common.presentation.PayWithNewCardUiState
import com.easypaysolutions.common.presentation.PayWithSavedCardUiState
import com.easypaysolutions.common.presentation.SheetFlow
import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.common.utils.ErrorMapper
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult
import com.easypaysolutions.utils.extensions.hide
import com.easypaysolutions.utils.extensions.hideKeyboard
import com.easypaysolutions.utils.extensions.show
import com.easypaysolutions.utils.validation.Validation
import com.easypaysolutions.utils.validation.ValidationState
import com.easypaysolutions.views.EasyPaySnackbar
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.FragmentAddNewCardBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

@Parcelize
internal data class AddNewCardInput(
    val flow: SheetFlow,
    val showCardSaveCheckbox: Boolean,
    val completeButtonLabel: String,
    @StringRes val infoLabelId: Int,
) : Parcelable

internal class AddNewCardFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AddNewCardFragment"
        const val INPUT_TAG = "AddNewCardInput"

        fun newInstance(input: AddNewCardInput): AddNewCardFragment {
            return AddNewCardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INPUT_TAG, input)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private val input: AddNewCardInput by lazy {
        arguments?.getParcelable<AddNewCardInput>(INPUT_TAG)
            ?: throw IllegalArgumentException("AddNewCardFragment called without input provided")
    }

    private val viewModel: AddNewCardViewModel by inject()
    private val sharedViewModel by activityViewModel<SheetViewModel>()

    private lateinit var binding: FragmentAddNewCardBinding

    //region Lifecycle

    init {
        initFlows()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddNewCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        setupBottomSheet()
    }

    //endregion

    //region Components initialization

    private fun initComponents() {
        binding.apply {
            sectionCardInformation.etCardHolderName.setText(sharedViewModel.accFullName())
            sectionBillingAddress.apply {
                etZip.setText(sharedViewModel.accZip())
                etStreetAddress.setText(sharedViewModel.accAddress())
            }

            newCardHeader.btnClose.setOnClickListener { actionClose() }
            sectionBottom.apply {
                btnComplete.apply {
                    setOnClickListener { actionComplete() }
                    text = input.completeButtonLabel
                    updateState(ValidationState.NotFilled)
                }
                tvInfoLabel.setText(input.infoLabelId)
                cbSaveCard.visibility =
                    if (input.showCardSaveCheckbox) View.VISIBLE else View.GONE
            }
            setupFieldsListeners()
            container.apply {
                setOnTouchListener { _, _ ->
                    hideKeyboard()
                    performClick()
                }
            }
        }
    }

    private fun setupBottomSheet() {
        // Behavior
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun setupFieldsListeners() {
        binding.apply {
            sectionCardInformation.apply {
                etCardNumber.setOnLostFocusListener { validateFields() }
                etCardHolderName.setOnLostFocusListener { validateFields() }
                etExpiryDate.setOnLostFocusListener { validateFields() }
                etCvc.setOnLostFocusListener { validateFields() }
            }
            sectionBillingAddress.apply {
                etStreetAddress.setOnLostFocusListener { validateFields() }
                etZip.setOnLostFocusListener { validateFields() }
            }
            sectionBottom.cbSaveCard.setOnCheckedChangeListener { _, _ -> validateFields() }
        }
    }

    //endregion

    //region Validation

    private fun validateFields() {
        val validationState = getValidationStateFromFields()
        viewModel.updateValidationState(validationState)
    }

    private fun getValidationStateFromFields(): ValidationState {
        binding.apply {
            return Validation.validateStates(
                sectionCardInformation.etCardNumber.validate(),
                sectionCardInformation.etCardHolderName.validate(),
                sectionCardInformation.etExpiryDate.validate(),
                sectionCardInformation.etCvc.validate(),
                sectionBillingAddress.etStreetAddress.validate(),
                sectionBillingAddress.etZip.validate()
            )
        }
    }

    //endregion

    //region Flows

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.validationState.collect { state ->
                        updateState(state)
                    }
                }
                launch {
                    sharedViewModel.payWithNewCardResult.collect { result ->
                        when (result) {
                            is PayWithNewCardUiState.Loading -> binding.progressView.show()

                            is PayWithNewCardUiState.Success -> {
                                binding.progressView.hide()
                                val completedResult = PaymentSheetResult.Completed(
                                    data = result.result,
                                    addedConsents = sharedViewModel.addedConsents,
                                    deletedConsents = sharedViewModel.deletedConsentIDs
                                )
                                sharedViewModel.completeWithResult(completedResult)
                            }

                            is PayWithNewCardUiState.Error -> {
                                binding.progressView.hide()
                                showPaymentGeneralError(result.exception)
                            }

                            is PayWithNewCardUiState.Declined -> {
                                binding.progressView.hide()
                                showPaymentDeclinedError()
                            }
                        }
                    }
                }
                launch {
                    sharedViewModel.addNewCardResult.collect { result ->
                        when (result) {
                            is AddNewCardUiState.Loading -> binding.progressView.show()

                            is AddNewCardUiState.Success -> {
                                binding.progressView.hide()
                                sharedViewModel.setShouldRefreshAfterClose()
                                when (input.flow) {
                                    SheetFlow.CARD_MANAGEMENT -> actionClose()

                                    // If the flow is CARD_PAYMENT, we need to pay with the saved card after saving it
                                    SheetFlow.CARD_PAYMENT -> sharedViewModel.payWithSavedCard(
                                        result.result.consentId
                                    )
                                }
                            }

                            is AddNewCardUiState.Error -> {
                                binding.progressView.hide()
                                showSaveGeneralError(result.exception)
                            }
                        }
                    }
                }
                // Payment with saved card flow will be triggered ONLY after saving the card in the CARD_PAYMENT flow
                launch {
                    sharedViewModel.payWithSavedCardResult.collect { state ->
                        when (state) {
                            is PayWithSavedCardUiState.Declined -> {
                                binding.progressView.hide()
                                showPaymentDeclinedError()
                                showAddNewCardSuccessSnackbar()
                            }

                            is PayWithSavedCardUiState.Error -> {
                                binding.progressView.hide()
                                showPaymentGeneralError(state.exception)
                                showAddNewCardSuccessSnackbar()
                            }

                            is PayWithSavedCardUiState.Loading -> binding.progressView.show()

                            is PayWithSavedCardUiState.Success -> {
                                binding.progressView.hide()
                                val completedResult = PaymentSheetResult.Completed(
                                    data = state.result,
                                    addedConsents = sharedViewModel.addedConsents,
                                    deletedConsents = sharedViewModel.deletedConsentIDs
                                )
                                sharedViewModel.completeWithResult(completedResult)
                            }
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Button actions

    private fun actionComplete() {
        val validationState = getValidationStateFromFields()
        if (validationState != ValidationState.Valid) {
            viewModel.updateValidationState(validationState)
            return
        }

        when (input.flow) {
            SheetFlow.CARD_PAYMENT -> sharedViewModel.payWithNewCard(prepareViewData())
            SheetFlow.CARD_MANAGEMENT -> sharedViewModel.addNewCard(prepareViewData())
        }
    }

    private fun actionClose() {
        sharedViewModel.closeNewCardSheet()
        dismiss()
    }

    //endregion

    //region Helpers

    private fun showAddNewCardSuccessSnackbar() {
        EasyPaySnackbar
            .makeSuccess(binding.root, R.string.card_was_saved)
            .show()
    }

    private fun updateState(state: ValidationState) {
        binding.sectionBottom.apply {
            btnComplete.updateState(state)
            tvInfoLabel.visibility =
                if (state == ValidationState.NotFilled || state == ValidationState.InternalValidationInvalid) View.VISIBLE else View.GONE
            tvMainError.visibility = View.GONE
        }
    }

    private fun prepareViewData(): AddNewCardViewData {
        return binding.run {
            AddNewCardViewData(
                last4digits = sectionCardInformation.etCardNumber.getText().takeLast(4),
                encryptedCardNumber = sectionCardInformation.etCardNumber.getSecureData(),
                cardHolderName = sectionCardInformation.etCardHolderName.getText(),
                expMonth = sectionCardInformation.etExpiryDate.getExpMonth(),
                expYear = sectionCardInformation.etExpiryDate.getExpYear(),
                cvv = sectionCardInformation.etCvc.getText(),
                streetAddress = sectionBillingAddress.etStreetAddress.getText(),
                zip = sectionBillingAddress.etZip.getText(),
                shouldSaveCard = sectionBottom.cbSaveCard.isChecked
            )
        }
    }

    //endregion

    //region Error handling

    private fun handleCustomError(
        exception: EasyPayWidgetException,
        @StringRes defaultMessageResId: Int,
    ) {
        ErrorMapper.getCustomErrorMessage(exception)?.let {
            showError(it)
        } ?: showError(defaultMessageResId)
    }

    private fun showSaveGeneralError(exception: EasyPayWidgetException) {
        handleCustomError(exception, R.string.unable_to_save_your_card)
    }

    private fun showPaymentDeclinedError() {
        showError(R.string.process_payment_declined_error)
    }

    private fun showPaymentGeneralError(exception: EasyPayWidgetException) {
        handleCustomError(exception, R.string.process_payment_general_error)
    }

    private fun showError(@StringRes messageResId: Int) {
        updateState(ValidationState.ExternalValidationInvalid)
        binding.sectionBottom.apply {
            tvMainError.setText(messageResId)
            tvMainError.visibility = View.VISIBLE
        }
    }

    //endregion

}

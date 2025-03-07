package com.easypaysolutions.common.presentation.manage_cards

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypaysolutions.common.presentation.DeleteCardUiState
import com.easypaysolutions.common.presentation.PayWithSavedCardUiState
import com.easypaysolutions.common.presentation.PaymentMethodsUiState
import com.easypaysolutions.common.presentation.SheetFlow
import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult
import com.easypaysolutions.utils.extensions.dpToPx
import com.easypaysolutions.utils.extensions.hide
import com.easypaysolutions.utils.extensions.show
import com.easypaysolutions.utils.extensions.showSheetPopup
import com.easypaysolutions.utils.validation.ValidationState
import com.easypaysolutions.views.EasyPaySnackbar
import com.easypaysolutions.views.SheetPopupInput
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.FragmentManageCardsBinding
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.activityViewModel

@Parcelize
internal data class ManageCardsInput(
    val flow: SheetFlow,
    val completeButtonLabel: String?,
    @StringRes val cardsSectionLabelId: Int,
) : Parcelable {

    @IgnoredOnParcel
    val showCompleteButton: Boolean = completeButtonLabel != null
}

internal class ManageCardsFragment : Fragment() {

    companion object {
        const val INPUT_TAG = "ManageCardsInput"

        fun newInstance(input: ManageCardsInput): ManageCardsFragment {
            return ManageCardsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INPUT_TAG, input)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private val input: ManageCardsInput by lazy {
        arguments?.getParcelable<ManageCardsInput>(INPUT_TAG)
            ?: throw IllegalArgumentException("ManageCardsFragment called without input provided")
    }

    private val cardsAdapter: CardsAdapter by lazy {
        CardsAdapter(
            listener = object : CardsAdapter.Listener {
                override fun onAddCardClick() {
                    onAddCardClicked()
                }

                override fun onSelectCardAction(annualConsent: AnnualConsent?) {
                    onCardSelected(annualConsent)
                }
            },
            initiallySelectedCardId = sharedViewModel.selectedCard?.id,
        )
    }

    private lateinit var binding: FragmentManageCardsBinding
    private val rvLayoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    private val sharedViewModel by activityViewModel<SheetViewModel>()

    //region Lifecycle

    init {
        initFlows()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentManageCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    //endregion

    //region Flows initialization

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.paymentMethods.collect { state ->
                        when (state) {
                            is PaymentMethodsUiState.Error -> {
                                sharedViewModel.completeWithError(state.exception)
                            }

                            is PaymentMethodsUiState.Loading -> { /* Do nothing */
                            }

                            is PaymentMethodsUiState.Success -> {
                                cardsAdapter.setCards(state.methods)
                            }
                        }
                    }
                }
                launch {
                    sharedViewModel.payWithSavedCardResult.collect { state ->
                        when (state) {
                            is PayWithSavedCardUiState.Declined -> {
                                binding.progressView.hide()
                                showDeclinedError()
                            }

                            is PayWithSavedCardUiState.Error -> {
                                binding.progressView.hide()
                                showGeneralError()
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
                launch {
                    sharedViewModel.deleteCardResult.collect { state ->
                        when (state) {
                            is DeleteCardUiState.Error -> {
                                binding.progressView.hide()
                                showDeleteCardErrorSnackbar()
                            }

                            is DeleteCardUiState.Loading -> binding.progressView.show()

                            is DeleteCardUiState.Success -> {
                                binding.progressView.hide()
                                showDeleteCardSuccessSnackbar()
                                setupCardSelection()
                            }
                        }
                    }

                }
            }
        }
    }

    //endregion

    //region Helpers

    private fun showGeneralError() {
        binding.layoutSelectedCardSection.tvError.setText(R.string.process_payment_general_error)
        showErrorText()
    }

    private fun showDeclinedError() {
        binding.layoutSelectedCardSection.tvError.setText(R.string.process_payment_declined_error)
        showErrorText()
    }

    private fun showErrorText() {
        binding.layoutSelectedCardSection.apply {
            tvError.visibility = View.VISIBLE
            btnComplete.updateState(ValidationState.ExternalValidationInvalid)
        }
    }

    private fun hideGeneralError() {
        binding.layoutSelectedCardSection.apply {
            tvError.visibility = View.GONE
            btnComplete.updateState(ValidationState.Valid)
        }
    }

    //endregion

    //region Components initialization

    private fun initComponents() {
        binding.apply {
            root.doOnNextLayout {
                updateRecyclerViewCardsLeftPadding()
            }
            background.setOnClickListener { cancelAction() }
            rvCards.apply {
                adapter = cardsAdapter
                layoutManager = rvLayoutManager
            }
            layoutSelectPaymentMethodSection.apply {
                tvTitle.setText(input.cardsSectionLabelId)
                btnClose.setOnClickListener { cancelAction() }
            }
            layoutSelectedCardSection.apply {
                btnComplete.apply {
                    setOnClickListener { completeAction() }
                    visibility = if (input.showCompleteButton) View.VISIBLE else View.GONE
                    text = input.completeButtonLabel
                }
                btnDeleteCard.setOnClickListener {
                    onDeleteCardClicked()
                }
            }
            setupCardSelection()
        }
    }

    private fun cancelAction() {
        when (input.flow) {
            SheetFlow.CARD_MANAGEMENT -> sharedViewModel.completeWithResult(
                CustomerSheetResult.Selected(
                    selectedConsentId = sharedViewModel.selectedCard?.id,
                    addedConsents = sharedViewModel.addedConsents,
                    deletedConsents = sharedViewModel.deletedConsentIDs
                )
            )

            SheetFlow.CARD_PAYMENT -> sharedViewModel.completeWithResult(PaymentSheetResult.Canceled)
        }
    }

    private fun completeAction() {
        sharedViewModel.selectedCard?.let {
            when (input.flow) {
                SheetFlow.CARD_MANAGEMENT -> {
                    /* Do nothing */
                }

                SheetFlow.CARD_PAYMENT -> sharedViewModel.payWithSavedCard(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupCardSelection() {
        binding.layoutSelectedCardSection.apply {
            sharedViewModel.selectedCard?.let {
                tvCardNumber.text = context?.getString(
                    R.string.hidden_card_format,
                    it.accountNumber
                )
                tvCardHolderName.text =
                    "${it.accountHolderFirstName} ${it.accountHolderLastName}"
                container.visibility = View.VISIBLE
            } ?: run {
                container.visibility = View.GONE
            }
        }
    }

    //endregion

    //region Actions

    private fun onCardSelected(selectedCard: AnnualConsent?) {
        hideGeneralError()
        sharedViewModel.onCardSelected(selectedCard)
        setupCardSelection()
    }

    private fun onAddCardClicked() {
        hideGeneralError()
        sharedViewModel.openNewCardSheet()
    }

    private fun onDeleteCardClicked() {
        showDeleteConfirmationSheetPopup()
    }

    //endregion

    //region Popups

    private fun showDeleteConfirmationSheetPopup() {
        val sheetPopupInput = SheetPopupInput(
            titleId = R.string.delete_this_card,
            descriptionId = R.string.delete_this_card_confirmation,
            primaryButtonLabelId = R.string.delete,
            secondaryButtonLabelId = R.string.cancel,
            primaryAction = { sharedViewModel.deleteSelectedCard() },
        )
        showSheetPopup(sheetPopupInput)
    }

    private fun showDeleteCardErrorSnackbar() {
        EasyPaySnackbar
            .makeFailure(binding.root, R.string.delete_card_failure_message)
            .show()
    }

    private fun showDeleteCardSuccessSnackbar() {
        EasyPaySnackbar
            .makeSuccess(binding.root, R.string.delete_card_success_message)
            .show()
    }

    //endregion

    //region Helpers

    /**
     * Update left padding of the cards recycler view to center the cards section
     */
    private fun updateRecyclerViewCardsLeftPadding() {
        val sectionWidth = binding.layoutSelectPaymentMethodSection.root.measuredWidth
        val containerWidth = binding.root.measuredWidth
        val margins = containerWidth - sectionWidth
        val leftPadding = (margins / 2) + 16.dpToPx(binding.root.context)
        binding.rvCards.updateLayoutParams<ConstraintLayout.LayoutParams> {
            binding.rvCards.updatePadding(leftPadding, 0, 0, 0)
            rvLayoutManager.scrollToPosition(0)
        }
    }

    //endregion

}
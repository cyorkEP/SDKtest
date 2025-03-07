package com.easypaysolutions.customer_sheet.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easypaysolutions.common.di.CommonDiModules
import com.easypaysolutions.common.presentation.AddNewCardUiState
import com.easypaysolutions.common.presentation.SheetActivity
import com.easypaysolutions.common.presentation.SheetFlow
import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardInput
import com.easypaysolutions.common.presentation.manage_cards.ManageCardsInput
import com.easypaysolutions.customer_sheet.di.CustomerSheetDiModules
import com.easypaysolutions.customer_sheet.utils.CustomerSheetContract
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult
import com.easypaysolutions.customer_sheet.utils.validate
import com.easypaysolutions.views.EasyPaySnackbar
import com.easypaysolutions.widgets.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf

internal class CustomerSheetActivity : SheetActivity<CustomerSheetResult>() {

    private val starterArgs: CustomerSheetContract.Args by lazy {
        CustomerSheetContract.Args.fromIntent(intent)
            ?: throw IllegalArgumentException("Sheet started without arguments.")
    }

    //region Overridden

    override val viewModel: SheetViewModel by viewModel {
        parametersOf(
            starterArgs.config
        )
    }

    override val addNewCardInput: AddNewCardInput by lazy {
        AddNewCardInput(
            flow = SheetFlow.CARD_MANAGEMENT,
            showCardSaveCheckbox = false,
            completeButtonLabel = getString(R.string.save_card),
            infoLabelId = R.string.complete_all_fields_to_save
        )
    }

    override val manageCardsInput: ManageCardsInput by lazy {
        ManageCardsInput(
            flow = SheetFlow.CARD_MANAGEMENT,
            completeButtonLabel = null,
            cardsSectionLabelId = R.string.your_cards
        )
    }

    override fun finishWithError(error: Throwable?) {
        val e = error ?: defaultInitializationError()
        finishWithResult(CustomerSheetResult.Failed(e))
    }

    override fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback {
            viewModel.completeWithResult(
                CustomerSheetResult.Selected(
                    selectedConsentId = viewModel.selectedCard?.id,
                    addedConsents = viewModel.addedConsents,
                    deletedConsents = viewModel.deletedConsentIDs
                )
            )
        }
    }

    override fun initKoinModules() {
        loadKoinModules(
            listOf(
                CommonDiModules.module,
                CustomerSheetDiModules.module
            )
        )
    }

    //endregion

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val validationResult = initArgs()

        val validatedArgs = validationResult.getOrNull()
        if (validatedArgs == null) {
            finishWithError(error = validationResult.exceptionOrNull())
            return
        }
    }

    //endregion

    //region Initialization

    private fun initArgs(): Result<CustomerSheetContract.Args?> {
        val starterArgs = this.starterArgs

        val result = try {
            starterArgs.config.validate()
            Result.success(starterArgs)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }

        return result
    }

    override fun initFlows() {
        super.initFlows()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.customerSheetResult.collect { result ->
                        finishWithResult(result)
                    }
                }
                launch {
                    viewModel.addNewCardResult.collect { result ->
                        when (result) {
                            is AddNewCardUiState.Success -> {
                                showAddNewCardSuccessSnackbar()
                            }

                            else -> {
                                /* Do nothing */
                            }
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Finish activity

    private fun finishWithResult(result: CustomerSheetResult) {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(CustomerSheetContract.Result(result).toBundle())
        )
        finish()
    }

    //endregion

    //region Helpers

    private fun showAddNewCardSuccessSnackbar() {
        EasyPaySnackbar
            .makeSuccess(binding.root, R.string.card_was_saved)
            .show()
    }

    private fun defaultInitializationError(): IllegalArgumentException {
        return IllegalArgumentException("Sheet started without arguments.")
    }

    //endregion

}
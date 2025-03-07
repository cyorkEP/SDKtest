package com.easypaysolutions.payment_sheet.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easypaysolutions.common.di.CommonDiModules
import com.easypaysolutions.common.presentation.SheetActivity
import com.easypaysolutions.common.presentation.SheetFlow
import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardInput
import com.easypaysolutions.common.presentation.manage_cards.ManageCardsInput
import com.easypaysolutions.payment_sheet.di.PaymentSheetDiModules
import com.easypaysolutions.payment_sheet.utils.PaymentSheetContract
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult
import com.easypaysolutions.payment_sheet.utils.validate
import com.easypaysolutions.utils.currency.CurrencyFormatter
import com.easypaysolutions.widgets.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf

internal class PaymentSheetActivity : SheetActivity<PaymentSheetResult>() {

    private val starterArgs: PaymentSheetContract.Args by lazy {
        PaymentSheetContract.Args.fromIntent(intent)
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
            flow = SheetFlow.CARD_PAYMENT,
            showCardSaveCheckbox = true,
            completeButtonLabel = getCompleteButtonLabel(),
            infoLabelId = R.string.complete_all_fields_to_pay
        )
    }

    override val manageCardsInput: ManageCardsInput by lazy {
        ManageCardsInput(
            flow = SheetFlow.CARD_PAYMENT,
            completeButtonLabel = getCompleteButtonLabel(),
            cardsSectionLabelId = R.string.select_payment_method
        )
    }

    override fun finishWithError(error: Throwable?) {
        val e = error ?: defaultInitializationError()
        finishWithResult(PaymentSheetResult.Failed(e))
    }

    override fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback {
            finishWithResult(PaymentSheetResult.Canceled)
        }
    }

    override fun initKoinModules() {
        loadKoinModules(
            listOf(
                CommonDiModules.module,
                PaymentSheetDiModules.module
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

    private fun initArgs(): Result<PaymentSheetContract.Args?> {
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
                    viewModel.paymentSheetResult.collect { result ->
                        finishWithResult(result)
                    }
                }
            }
        }
    }

    //endregion

    //region Finish activity

    private fun finishWithResult(result: PaymentSheetResult) {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(PaymentSheetContract.Result(result).toBundle())
        )
        finish()
    }

    //endregion

    //region Helpers

    private fun defaultInitializationError(): IllegalArgumentException {
        return IllegalArgumentException("PaymentSheet started without arguments.")
    }

    private fun getCompleteButtonLabel(): String {
        return applicationContext.getString(
            R.string.pay_format,
            CurrencyFormatter.formatCurrency(starterArgs.config.amounts.totalAmount)
        )
    }

    //endregion

}
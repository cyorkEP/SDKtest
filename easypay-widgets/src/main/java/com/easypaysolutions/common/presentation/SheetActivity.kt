package com.easypaysolutions.common.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardFragment
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardInput
import com.easypaysolutions.common.presentation.manage_cards.ManageCardsFragment
import com.easypaysolutions.common.presentation.manage_cards.ManageCardsInput
import com.easypaysolutions.utils.extensions.hide
import com.easypaysolutions.utils.extensions.show
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.ActivityPaymentSheetBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal abstract class SheetActivity<ResultType> : AppCompatActivity() {

    abstract val viewModel: SheetViewModel
    abstract val addNewCardInput: AddNewCardInput
    abstract val manageCardsInput: ManageCardsInput

    abstract fun finishWithError(error: Throwable?)
    abstract fun setupOnBackPressed()
    abstract fun initKoinModules()

    protected lateinit var binding: ActivityPaymentSheetBinding
    private lateinit var cardsJob: Job

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnBackPressed()
        initFlows()
        initKoinModules()
    }

    //endregion

    //region Initialization

    open fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardsJob = launch {
                    viewModel.paymentMethods.collect { state ->
                        when (state) {
                            is PaymentMethodsUiState.Success -> {
                                binding.mainProgressView.hide()
                                openManageCardsSheet()
                                cardsJob.cancel()
                            }

                            is PaymentMethodsUiState.Error -> {
                                binding.mainProgressView.hide()
                                finishWithError(state.exception)
                            }

                            is PaymentMethodsUiState.Loading -> binding.mainProgressView.show()
                        }
                    }
                }
                launch {
                    viewModel.openNewCardSheet.collect { state ->
                        if (state is OpenNewCardSheetUiState.Open) {
                            openAddNewCardSheet()
                        }
                    }
                }
                launch {
                    viewModel.errorState.collect { error ->
                        finishWithError(error)
                    }
                }
            }
        }
    }

    //endregion

    //region Navigation

    private fun openAddNewCardSheet() {
        val fragment = AddNewCardFragment.newInstance(addNewCardInput)
        fragment.show(supportFragmentManager, AddNewCardFragment.TAG)
    }

    private fun openManageCardsSheet() {
        val fragment = ManageCardsFragment.newInstance(manageCardsInput)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.commit()
    }

    //endregion

}
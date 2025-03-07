package com.easypaysolutions.payment_sheet.di

import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.payment_sheet.PaymentSheet
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal object PaymentSheetDiModules {
    val module = module {

        // ViewModels
        viewModel { (config: PaymentSheet.Configuration) ->
            SheetViewModel(
                config,
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }
}
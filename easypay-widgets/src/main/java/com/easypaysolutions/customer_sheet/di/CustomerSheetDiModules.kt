package com.easypaysolutions.customer_sheet.di

import com.easypaysolutions.common.presentation.SheetViewModel
import com.easypaysolutions.customer_sheet.CustomerSheet
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal object CustomerSheetDiModules {
    val module = module {

        // ViewModels
        viewModel { (config: CustomerSheet.Configuration) ->
            SheetViewModel(
                config,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }
}
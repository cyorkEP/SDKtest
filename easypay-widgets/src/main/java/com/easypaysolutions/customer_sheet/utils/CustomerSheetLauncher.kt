package com.easypaysolutions.customer_sheet.utils

import com.easypaysolutions.customer_sheet.CustomerSheet

internal interface CustomerSheetLauncher {
    fun present(configuration: CustomerSheet.Configuration? = null)
}
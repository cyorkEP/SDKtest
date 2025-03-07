package com.easypaysolutions.customer_sheet.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.easypaysolutions.customer_sheet.CustomerSheet

internal class DefaultCustomerSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<CustomerSheetContract.Args>,
    private val callback: CustomerSheetResultCallback,
) : CustomerSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: CustomerSheetResultCallback,
    ) : this(
        activityResultLauncher = activity.registerForActivityResult(
            CustomerSheetContract()
        ) {
            callback.onCustomerSheetResult(it)
        },
        callback = callback,
    )

    constructor(
        fragment: Fragment,
        callback: CustomerSheetResultCallback,
    ) : this(
        activityResultLauncher = fragment.registerForActivityResult(
            CustomerSheetContract()
        ) {
            callback.onCustomerSheetResult(it)
        },
        callback = callback,
    )

    override fun present(configuration: CustomerSheet.Configuration?) {
        val args = CustomerSheetContract.Args(
            config = configuration ?: CustomerSheet.Configuration()
        )

        try {
            activityResultLauncher.launch(args)
        } catch (e: IllegalStateException) {
            val message = "Error launching CustomerSheet. " +
                    "Make sure you are launching the CustomerSheet from an Activity or Fragment."
            callback.onCustomerSheetResult(
                CustomerSheetResult.Failed(
                    IllegalStateException(
                        message,
                        e
                    )
                )
            )
        }
    }

}
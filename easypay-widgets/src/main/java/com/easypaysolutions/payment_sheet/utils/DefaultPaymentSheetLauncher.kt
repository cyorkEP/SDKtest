package com.easypaysolutions.payment_sheet.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.easypaysolutions.payment_sheet.PaymentSheet

internal class DefaultPaymentSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<PaymentSheetContract.Args>,
    private val callback: PaymentSheetResultCallback,
) : PaymentSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: PaymentSheetResultCallback,
    ) : this(
        activityResultLauncher = activity.registerForActivityResult(
            PaymentSheetContract()
        ) {
            callback.onPaymentSheetResult(it)
        },
        callback = callback,
    )

    constructor(
        fragment: Fragment,
        callback: PaymentSheetResultCallback,
    ) : this(
        activityResultLauncher = fragment.registerForActivityResult(
            PaymentSheetContract()
        ) {
            callback.onPaymentSheetResult(it)
        },
        callback = callback,
    )

    override fun present(configuration: PaymentSheet.Configuration?) {
        val args = PaymentSheetContract.Args(
            config = configuration ?: PaymentSheet.Configuration(),
        )

        try {
            activityResultLauncher.launch(args)
        } catch (e: IllegalStateException) {
            val message = "Error launching PaymentSheet. " +
                    "Make sure you are launching the PaymentSheet from an Activity or Fragment."
            callback.onPaymentSheetResult(
                PaymentSheetResult.Failed(
                    IllegalStateException(
                        message,
                        e
                    )
                )
            )
        }
    }
}

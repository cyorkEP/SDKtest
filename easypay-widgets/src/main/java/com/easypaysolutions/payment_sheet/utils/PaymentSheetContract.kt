package com.easypaysolutions.payment_sheet.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.payment_sheet.presentation.PaymentSheetActivity
import com.easypaysolutions.common.presentation.SheetFlow
import kotlinx.parcelize.Parcelize

internal class PaymentSheetContract :
    ActivityResultContract<PaymentSheetContract.Args, PaymentSheetResult>() {

    override fun createIntent(context: Context, input: Args): Intent {
        return Intent(context, PaymentSheetActivity::class.java)
            .putExtra(EXTRA_ARGS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PaymentSheetResult {
        @Suppress("DEPRECATION")
        val paymentResult = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.paymentSheetResult
        return paymentResult ?: PaymentSheetResult.Failed(
            IllegalArgumentException("Failed to retrieve a PaymentSheetResult.")
        )
    }

    @Parcelize
    internal data class Result(
        val paymentSheetResult: PaymentSheetResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    @Parcelize
    data class Args(
        internal val config: PaymentSheet.Configuration,
    ) : Parcelable {
        companion object {
            internal fun fromIntent(intent: Intent): Args? {
                @Suppress("DEPRECATION")
                return intent.getParcelableExtra(EXTRA_ARGS)
            }
        }
    }

    internal companion object {
        const val EXTRA_ARGS =
            "com.easypaysolutions.payment_sheet.utils.PaymentSheetContract.extra_args"
        private const val EXTRA_RESULT =
            "com.easypaysolutions.payment_sheet.utils.PaymentSheetContract.extra_result"
    }
}
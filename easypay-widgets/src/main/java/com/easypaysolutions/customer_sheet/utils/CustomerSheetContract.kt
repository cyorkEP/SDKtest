package com.easypaysolutions.customer_sheet.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.customer_sheet.presentation.CustomerSheetActivity
import kotlinx.parcelize.Parcelize

internal class CustomerSheetContract :
    ActivityResultContract<CustomerSheetContract.Args, CustomerSheetResult>() {

    override fun createIntent(context: Context, input: Args): Intent {
        return Intent(context, CustomerSheetActivity::class.java)
            .putExtra(EXTRA_ARGS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CustomerSheetResult {
        @Suppress("DEPRECATION")
        val customerResult = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.paymentSheetResult
        return customerResult ?: CustomerSheetResult.Failed(
            IllegalArgumentException("Failed to retrieve a CustomerSheetResult.")
        )
    }

    @Parcelize
    internal data class Result(
        val paymentSheetResult: CustomerSheetResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    @Parcelize
    data class Args(
        internal val config: CustomerSheet.Configuration
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
            "com.easypaysolutions.customer_sheet.utils.CustomerSheetContract.extra_args"
        private const val EXTRA_RESULT =
            "com.easypaysolutions.customer_sheet.utils.CustomerSheetContract.extra_result"
    }
}
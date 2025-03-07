package com.easypaysolutions.payment_sheet.utils

import com.easypaysolutions.payment_sheet.PaymentSheet

internal fun PaymentSheet.Configuration.validate() {
    // These are not localized as they are not intended to be displayed to a user.
    when {
        amounts.totalAmount <= 0.0 -> {
            throw IllegalArgumentException(
                "Total amount must be greater than 0."
            )
        }

        consentCreator.merchantId <= 0 -> {
            throw IllegalArgumentException(
                "Consent creator merchant ID must be greater than 0."
            )
        }

        consentCreator.limitLifeTime <= 0.0 -> {
            throw IllegalArgumentException(
                "Consent creator limit life time must be greater than 0."
            )
        }

        consentCreator.limitPerCharge <= 0.0 -> {
            throw IllegalArgumentException(
                "Consent creator limit per charge must be greater than 0."
            )
        }

        consentCreator.customerReferenceId.isNullOrBlank() && consentCreator.rpguid.isNullOrBlank() -> {
            throw IllegalArgumentException(
                "Consent creator customer reference ID or RPGUID must be provided."
            )
        }
    }
}
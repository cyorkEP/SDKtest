package com.easypaysolutions.customer_sheet.utils

import com.easypaysolutions.customer_sheet.CustomerSheet

internal fun CustomerSheet.Configuration.validate() {
    // These are not localized as they are not intended to be displayed to a user.
    when {
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
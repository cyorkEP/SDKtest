package com.easypaysolutions.utils.validation

import com.easypaysolutions.utils.date.DateUtils

internal object Validation {

    private const val CORRECT_MASKED_EXP_DATE_LENGTH = 5

    fun isRegexValid(value: String, regex: String): Boolean {
        return value.matches(Regex(regex))
    }

    /**
     * Pass expiration date in MM/yy format
     */
    fun isExpirationDateNotExpired(expirationDate: String): Boolean {
        return try {
            val expiryDate = DateUtils.parseExpirationDate(expirationDate)
            val latestInvalidDate = DateUtils.getLatestInvalidExpDate()
            latestInvalidDate?.before(expiryDate) ?: false
        } catch (exception: Exception) {
            false
        }
    }

    /**
     * Pass expiration date in MM/yy format
     */
    fun isExpirationDateFormatValid(expirationDate: String): Boolean {
        val lengthValid = expirationDate.length == CORRECT_MASKED_EXP_DATE_LENGTH
        if (!lengthValid) {
            return false
        }

        try {
            val expiryDate = DateUtils.parseExpirationDate(expirationDate)
            return expiryDate != null
        } catch (exception: Exception) {
            return false
        }
    }

    /**
     * ExternalValidationInvalid cannot be passed from the fields.
     */
    fun validateStates(vararg states: ValidationState): ValidationState {
        return if (states.contains(ValidationState.NotFilled)) {
            ValidationState.NotFilled
        } else if (states.contains(ValidationState.InternalValidationInvalid)) {
            ValidationState.InternalValidationInvalid
        } else {
            ValidationState.Valid
        }
    }
}
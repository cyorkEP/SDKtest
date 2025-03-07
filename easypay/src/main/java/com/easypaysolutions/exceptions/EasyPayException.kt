package com.easypaysolutions.exceptions

import com.easypaysolutions.api.EasyPayApiError

/**
 * A base class for EasyPay-related exceptions.
 */
abstract class EasyPayException(
    val easyPayError: EasyPayApiError? = null,
    message: String? = easyPayError?.message,
    cause: Throwable? = null,
) : Exception(message, cause)
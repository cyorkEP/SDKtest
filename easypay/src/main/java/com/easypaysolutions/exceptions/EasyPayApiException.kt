package com.easypaysolutions.exceptions

import com.easypaysolutions.api.EasyPayApiError

class EasyPayApiException(
    easyPayError: EasyPayApiError? = null,
    message: String? = easyPayError?.message,
    cause: Throwable? = null,
) : EasyPayException(easyPayError, message, cause)

package com.easypaysolutions.common.exceptions

import com.easypaysolutions.exceptions.EasyPayApiException
import com.easypaysolutions.exceptions.EasyPayException

/**
 * [EasyPayWidgetException] - a class for exceptions issued in the UI Widgets.
 */
internal class EasyPayWidgetException(
    easyPayApiException: EasyPayApiException? = null,
    message: String? = easyPayApiException?.easyPayError?.message,
    cause: Throwable? = null,
) : EasyPayException(easyPayApiException?.easyPayError, message, cause)
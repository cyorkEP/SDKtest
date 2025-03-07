package com.easypaysolutions.common.utils

import androidx.annotation.StringRes
import com.easypaysolutions.common.exceptions.EasyPayWidgetException
import com.easypaysolutions.widgets.R

internal object ErrorMapper {

    private enum class ErrorCodeMessages(val code: Int, @StringRes val message: Int) {
        CANNOT_VALIDATE_CARD_INFO(55716, R.string.api_error_invalid_credit_card_data),
    }

    fun getCustomErrorMessage(exception: EasyPayWidgetException): Int? {
        val errorCode = exception.easyPayError?.code ?: return null
        return ErrorCodeMessages.entries.firstOrNull { it.code == errorCode }?.message
    }
}
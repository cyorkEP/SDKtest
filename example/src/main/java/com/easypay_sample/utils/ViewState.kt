package com.easypay_sample.utils

sealed class ViewState<T>(
    val value: T? = null,
    val message: String? = null,
    val exception: Exception? = null,
) {
    class Success<T>(data: T) : ViewState<T>(data)
    class Error<T>(
        message: String? = null,
        exception: Exception? = null,
        data: T? = null,
    ) : ViewState<T>(data, message, exception)

    class Loading<T> : ViewState<T>()
}
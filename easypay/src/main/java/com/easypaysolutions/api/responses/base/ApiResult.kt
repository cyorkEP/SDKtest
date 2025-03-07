package com.easypaysolutions.api.responses.base

abstract class ApiResult internal constructor(
    open val functionOk: Boolean,
    open val errorCode: Int,
    open val errorMessage: String,
    open val responseMessage: String,
) {
    internal open fun parseIfNeeded() {}
}
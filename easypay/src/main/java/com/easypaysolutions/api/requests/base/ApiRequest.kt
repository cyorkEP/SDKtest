package com.easypaysolutions.api.requests.base

internal abstract class ApiRequest<T>(
    open val userDataPresent: Boolean,
    open val body: T
)
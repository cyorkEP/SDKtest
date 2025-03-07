package com.easypaysolutions.repositories

import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.networking.NetworkResource

internal interface Validator<X : BaseBodyParams> {
    suspend fun <T : ApiResult> validate(
        params: X,
        call: suspend () -> NetworkResource<T>,
    ): NetworkResource<T>
}
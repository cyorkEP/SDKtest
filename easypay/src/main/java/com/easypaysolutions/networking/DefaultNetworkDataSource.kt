package com.easypaysolutions.networking

import com.easypaysolutions.api.EasyPayApiError
import com.easypaysolutions.api.responses.base.ApiResponse
import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.api.responses.base.TransactionResult
import com.easypaysolutions.exceptions.EasyPayApiException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.sentry.Sentry
import okhttp3.ResponseBody
import retrofit2.Response

internal class DefaultNetworkDataSource : NetworkDataSource {
    override suspend fun <T : ApiResult, X : ApiResponse<T>> getResult(
        call: suspend () -> Response<X>,
    ): NetworkResource<T> {
        try {
            val response: Response<X> = call()
            if (!response.isSuccessful) {
                val error: NetworkResource<T> = error(
                    response.code(),
                    response.message(),
                    response.errorBody()
                )
                error.error?.let {
                    Sentry.captureException(it)
                }
                return error
            }

            val body = response.body() ?: return error(0, "empty_response", null)
            val result = body.result
            result.parseIfNeeded()

            if (!result.functionOk) {
                return error(result.errorCode, result.errorMessage, null)
            }

            return NetworkResource.success(result)
        } catch (e: Exception) {
            Sentry.captureException(e)
            return error(0, e.message ?: e.toString(), null)
        }
    }

    override suspend fun <T : TransactionResult, X : ApiResponse<T>> getTransactionResult(
        call: suspend () -> Response<X>,
    ): NetworkResource<T> {
        val resource = getResult(call)
        if (resource.status == NetworkResource.Status.ERROR) {
            return resource
        }

        if (resource.data?.txApproved == false) {
            return NetworkResource.declined(resource.data)
        }

        return resource
    }

    private fun <T> error(
        code: Int,
        message: String,
        errorBody: ResponseBody?,
    ): NetworkResource<T> {
        var msg = message
        errorBody?.let {
            msg = it.charStream().readText()
            try {
                val type = object : TypeToken<ResponseError?>() {}.type
                val errorResponse: ResponseError? = Gson().fromJson(msg, type)
                errorResponse?.error?.let { e -> msg = e }
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }

        val e = EasyPayApiException(
            EasyPayApiError(code, msg)
        )

        return NetworkResource.error(e)
    }

    private data class ResponseError(val error: String?)
}
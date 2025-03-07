package com.easypaysolutions.networking

import com.easypaysolutions.exceptions.EasyPayApiException

data class NetworkResource<out T>(
    val status: Status,
    val data: T?,
    val error: EasyPayApiException?,
) {

    enum class Status {
        SUCCESS,
        ERROR,
        DECLINED
    }

    companion object {
        fun <T> success(data: T): NetworkResource<T> {
            return NetworkResource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: EasyPayApiException, data: T? = null): NetworkResource<T> {
            return NetworkResource(Status.ERROR, data, error)
        }

        fun <T> declined(data: T? = null): NetworkResource<T> {
            return NetworkResource(Status.DECLINED, data, null)
        }
    }
}
package com.easypaysolutions.networking.interceptors

import okhttp3.Interceptor
import okhttp3.Response

internal class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.header("Content-Type", "application/json");
        return chain.proceed(builder.build())
    }
}
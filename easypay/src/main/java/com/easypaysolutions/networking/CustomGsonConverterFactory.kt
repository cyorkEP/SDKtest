package com.easypaysolutions.networking

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class CustomGsonConverterFactory(private val gson: Gson) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return Converter<ResponseBody, Any> { value ->
            val json = value.string().replace("\\\\", "\\")
            gson.fromJson(json, type)
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        return Converter<Any, RequestBody> { value ->
            val json = gson.toJson(value).replace("\\\\", "\\")
            val mediaType = "application/json; charset=UTF-8".toMediaType()
            json.toRequestBody(mediaType)
        }
    }
}